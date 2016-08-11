#!/usr/bin/env python
"""
StreamIt regression test.
Execute list of StreamIt programs on list of MPSoC configurations 
and compare the output streams of sequential, parallel x86/pthread
and mpsoc simulator against reference output from the MIT compiler.
All tasks are distributed across the synthesis cluster.
Usage:
* edit program_name_list, mpsoc_config_list and partitioner_list
* execute script ./reg_test.py
"""
__author__ = "Gregor Sievers, Martin Flasskamp"
__maintainer__ = "Martin Flasskamp"
import threading
import Queue
import time, random
import subprocess
import datetime
import re
import os
WORKERS = 40 #number of parallel threads

#regression test configurations
#
#programs with longer runtime should be listed first
# 'VectAdd', 'FFT4', 'Lattice', 'Sender80211', 'FilterBankNewInt', 'BitonicSort', 'AutoCor', 'BatcherSort', 'FFT2'
program_name_list = ['DES', 'FFT2','FFT3', 'BitonicSortRecursive', 'BubbleSort064', 'ComparisonCounting', 'InsertionSort', 'MergeSort', 'RadixSort']
#program_name_list = ['AutoCor']
#configs with longer runtime/more CPUs should be listed first
#mpsoc_config_list = ['4x4x8', '2x2x16', '4x2x8', '2x1x16', '2x2x8', '2x1x8', '2x2x4', '4x4x1', '2x2x2', '1x1x16', '1x1x8', '1x1x4', '1x1x2', '1x1x1']
#mpsoc_config_list = ['2x2x8', '2x1x8', '1x1x16', '1x1x8', '1x1x1']
mpsoc_config_list = ['1x1x2_tcdm','1x1x4_tcdm','1x1x16_tcdm','1x1x32_tcdm']
mpsoc_config_list = ['1x1x4_tcdm']

partitioner_list = ['sa:sim','greedy:sim','mit:sim']
partitioner_list = ['sa:sim']
mem_usage_list = ['0.25','0.5','0.75']
mem_usage_list = ['0.075']
interconnect_list = [0, 1]
interconnect_list = [1]
mem_layout_list = ['local', 'hybrid', 'shared']
#mem_layout_list = ['shared']
tcdm_banks_list = ['01','2','4','8','16','32','64','128']
tcdm_banks_list = ['4','8','16']
#tcdm_banks_list = ['1']


## debug: simple config
#program_name_list = ['VectAdd']
#mpsoc_config_list = ['1x1x2', '1x1x1']
#partitioner_list = ['greedy:sim']

steady_iterations = 30
max_mult = 20
sa_timeout = 72
sa_iterations = 2
merge = '-merge'
tcdm_size = 1024*128 # total TCDM size in kB

date = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M")
pr = subprocess.Popen("git describe --dirty --always", shell = True, stdout = subprocess.PIPE)
out = pr.communicate()
git_version = str(out[0]).strip('\n')
build_dir = 'reg_test/' + date + '-' + git_version + '/'

#HOST_LIST = 'cadburry lindt milka powerbar sarotti trumpf kitkat hanuta knoppers pickup raider twix bounty corny'
HOST_LIST = 'cadburry lindt powerbar sarotti trumpf kitkat hanuta knoppers pickup raider twix'
#older, slower server, no hyperthreading-> adjust -m option below to 95 (server have no load most of the time)
#HOST_LIST += ' nuts bluna copeo fanta mirinda perrier punica'


#object for parameter of worker thread
class param_obj(object):
# constructor:
		def __init__(self, program_name, mpsoc_config, partition, interconnect, mem_usage, mem_layout, tcdm_banks):
				self.program_name = program_name
				self.mpsoc_config = mpsoc_config
				self.partition = partition
				self.interconnect = interconnect
				self.mem_usage = mem_usage
				self.mem_layout = mem_layout
				self.tcdm_banks = tcdm_banks

class Worker(threading.Thread):

		def __init__(self, queue):
				self.__queue = queue
				threading.Thread.__init__(self)

		def run(self):
				while 1:
						param = self.__queue.get() # pop job from queue
						if param is None:
								break # reached end of queue

						args = ['sct-sub']
						args += ['-q', '-l', HOST_LIST]
						#CPU load threshold to prevent overload
						#Server have 2xhyperthreading, so 40% means 80% load on 'real' CPU cores
						args += ['-m', '40'] 
						args += ['module load coreva_usr &> /dev/null;']
						args += ['export TERM=xterm;']
						args += ['CPPFLAGS=\"$CPPFLAGS -DSTEADY_ITERATIONS=%s\"' % steady_iterations] 
						args += ['COREVA_MPSOC_SIMFLAGS=\"--args=\'-c %d --tcdm-size=%s --tcdm-banks=%s\'\"' % (param.interconnect,tcdm_size , param.tcdm_banks)]
						args += ['nice', 'ionice -c2 -n7', 'make']
						args += ['STR_BUILD_DIR=%s' % build_dir]
						args += ['WHITELIST=tests/%s.str' % param.program_name]
						args += ['STREAMIT_FLAGS=\"-xml=config/mpsoc_%s.xml -part=%s -satimeout=%d -resa=%d -maxmult=%d -savepart --max-mem=%s -ic=%d %s --memlayout=%s tcdm%s\"' % (param.mpsoc_config, param.partition, sa_timeout, sa_iterations, max_mult, param.mem_usage , param.interconnect, merge, param.mem_layout, param.tcdm_banks)]
						args += ['run_test']
						#args += ['2> /dev/null'] # ignore stderr
						#print args # debug
						subprocess.call(args)


###main program

# build streamit
subprocess.call(['make', '-s', 'streamit_build'])
# build dependencies
subprocess.call(['make', '-s', 'rebuild_deps'])

# print legend
print "Now we are in this file"
print '-started:   '+date
print 'build dir: '+build_dir
print '          program          |      build      |       run       |        compare        |'
print '                           | seq | pth | cva | seq | pth | cva | s-r | s-p | p-c | s-c |'

queue = Queue.Queue(0)

for i in range(WORKERS):
		Worker(queue).start() # start a worker


for partition in partitioner_list:
	for program_name in program_name_list:
		for mpsoc_config in mpsoc_config_list:
			if (mpsoc_config == '1x1x1'):
				params = param_obj(program_name, mpsoc_config, partition, interconnect_list[0], mem_usage_list[0], mem_layout_list[0], tcdm_banks_list[0])
				queue.put(params) #push job to queue/worker
				time.sleep(0.5) #sleep 1s
				continue
			for interconnect in interconnect_list:
				for memusage in mem_usage_list:
					for mem_layout in mem_layout_list:
						for tcdm_banks in tcdm_banks_list:
							params = param_obj(program_name, mpsoc_config, partition, interconnect, memusage, mem_layout, tcdm_banks)
							queue.put(params) #push job to queue/worker
							time.sleep(0.5) #sleep 1s

for i in range(WORKERS):
		queue.put(None) # add end-of-queue markers



# create def file for plotting
# create build dir
if not os.path.exists(build_dir):
		os.makedirs(build_dir)
# write legend definition for plot
plot_def = open(build_dir+'plot.def','w +')
plot_def.write('title='+date+'\nspeedup=1\ngrid=1\ndrop=5\nprograms_blacklist=\n');
# sort mpsoc_config_list by x, y and id
mpsoc_config_list.sort(key=lambda str: (int(re.sub("([0-9]+)x([0-9]+)x([0-9]+)(_tcdm)?", "\\1", str)), int(re.sub("([0-9]+)x([0-9]+)x([0-9]+)(_tcdm)?", "\\2", str)), int(re.sub("([0-9]+)x([0-9]+)x([0-9]+)(_tcdm)?", "\\3", str))));
# sort partitioner_list alphabetically
partitioner_list.sort()
#plot_def.write("mpsoc_config=[%s]\n" % ', '.join(map(str, mpsoc_config_list)))
#plot_def.write("partitioners=[%s]\n" % ', '.join(map(str, partitioner_list)))
for partition in partitioner_list:
		for mpsoc_config in mpsoc_config_list:
			for interconnect in interconnect_list:
				# create unique id from parameters
				streamit_flags = '-xml=config/mpsoc_%s.xml -part=%s -satimeout=%d -resa=%d -maxmult=%d -savepart -ic=%d %s' % (mpsoc_config, partition, sa_timeout, sa_iterations, max_mult, interconnect, merge)
				streamit_flags_id = re.sub('[^0-9a-zA-Z]+', '', streamit_flags)
				streamit_flags_id = re.sub('xml|part', '', streamit_flags_id)
				# append id to def file
				plot_def = open(build_dir+'plot.def','a+')
				plot_def.write(streamit_flags_id + ': ' + mpsoc_config + '_' + re.sub('[^0-9a-zA-Z]+','',partition) + '\n')
plot_def.close()
