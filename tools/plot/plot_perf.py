#!/usr/bin/env python
# usage: plot_perf.py [directory to plot] [config (def file)]
# possible def file options: 
# 	- grid
#	- speedup
#	- errorbars
#   - drop=[n]
#   - num=[n]

import matplotlib as mpl
#use non interactive backend
mpl.use('Agg') 
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.cm as cm
import glob
import os
import sys
import csv
import copy
from collections import namedtuple
#output colors
class tcolor:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARN = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'

##helper functions##
def readOutputItemFile(filename):
	print filename
	f = open(filename,'r')
	#skip all lines (go to last line)
	for line in f: 
		pass
	if not 'outputItems' in line:
		print tcolor.FAIL + 'Warning: mailformed txt (' + file + ')' + tcolor.ENDC
		sys.exit(-1)
	return line[line.rfind('outputItems')+12:line.rfind(')')].split(",")[0]
	
def readSimulatorFile(filename):
	values = []
	f = open(sim_file,'r')
	for line in f:
		if not line.endswith(";10\n"):
			continue
		values.append(float(line.split(';')[0]))
	result = []
	for i in range(1,len(values)): #sry this could probably written with lambda
		result.append(values[i]-values[i-1])
	return result
	
##check parameters##
if len(sys.argv)!=3:
	print "You need 2 parameters (csv directory + config)"
	sys.exit(-1)	

rootdir = sys.argv[1]
if not os.path.isdir(rootdir):
	print "First parameter must point to a directory"
	sys.exit(-1)

configfile = sys.argv[2]
if not os.path.isfile(configfile):
	print "Second parameter must point to config"
	sys.exit(-1)

if not configfile.endswith('.def'):
	print "Second parameter must end with .def"
	sys.exit(-1)

##load config file##
config = open(configfile,'r')
configlines = config.readlines()

##define output files##
outfile = os.path.basename(configfile).replace('.def','.png')
outfile_csv = os.path.basename(configfile).replace('.def','.csv')
os.chdir(rootdir)
print tcolor.OKGREEN + 'cd to ' + rootdir + tcolor.ENDC

partitioners = []
partitioners_name = []

##parse config file##
#all options we should use are stored in active options
active_options = []
title = ""
drop = 0
num=sys.maxint
programs_blacklist = []
for configline in configlines:
	if '=' in configline:
		data = configline.split('=')
		name = data[0].strip().lower()
		if name == 'title':
			title = data[1]
		if name == 'drop':
			drop = int(data[1])
		if name == 'num':
			num = int(data[1])
		if name == 'programs_blacklist':
			programs_blacklist = [x.strip() for x in data[1].split(',')]
			if len(programs_blacklist)>0 and programs_blacklist[0]:
				print "programs blacklist: "+', '.join(map(str, programs_blacklist))
		if len(data)==2 and data[1].startswith('1'):
			print "active option: "+name
			active_options.append(name)
	else: 
		if not os.path.isdir(configline.split(':')[0].rstrip()):
			print configline.split(':')[0].rstrip() + ' partitioner not found -> skipping'
			continue
		partitioners.append(configline.split(':')[0].rstrip())
		partitioners_name.append((configline.split(':')[1]).strip())

# drop has to be smaller than number of elements to plot
assert num > drop

if len(partitioners) == 0:
	print tcolor.FAIL + 'No partitioners to plot, aborting' + tcolor.ENDC
	sys.exit(-1)
allConfigurations 	 = os.listdir(partitioners[0]) #all configurations to plot
allConfigurations.sort()
# remove programs on blacklist
allConfigurations[:]=[x for x in allConfigurations if x not in programs_blacklist]
allConfigurations.sort()


##read csv files / calc min,max,avg,mean values ##
part_conf_values_var = []  #for each partitioner -> for each configuration -> the sample variance value
part_conf_values = []      #for each partitioner -> for each configuration -> the avg value
part_conf_outputItems = [] #for each partitioner -> for each configuration -> the num output item
for parts in partitioners:
	tc_values_var = []
	tc_values = [] #all avg values for the actual partitioner
	tc_outputItems = []
	for configuration in allConfigurations:
		file = parts+"/"+configuration
		if not os.path.isdir(file):
			print tcolor.WARN + file + " doesn't exist -> skipping" + tcolor.ENDC
			tc_values.append(0)
			continue
		sim_files = glob.glob(file+'/sim/trace/sim*.csv')
		print 'reading ' + file + ' (found ' + str(len(sim_files)) + ' csv files)'
		bench_avg   = 0; # for one part/conf the highest avg value for all simulation files
		raw_values = []
		for sim_file in sim_files:
			values = readSimulatorFile(sim_file)[drop:num]
			avg=sum(values)/len(values)
			bench_avg = max(bench_avg, avg)
			raw_values += values
		#read output item file
		file = parts + '/' + configuration + "/PerfEstimate.txt"
		if not os.path.isfile(file):
			print tcolor.WARN + 'Warning: ' + file + ' not found skipping' + tcolor.ENDC
			tc_values.append(0)
			continue
		outputItems = readOutputItemFile(file)
		#calc mean error
		var = 0
		for rawValue in raw_values:
			var += (rawValue - bench_avg) * (rawValue - bench_avg)
		var /= len(raw_values)
		#create data structure 
		tc_values_var.append(var);
		tc_values.append(bench_avg)
		tc_outputItems.append(outputItems)
	part_conf_values.append(tc_values)
	part_conf_outputItems.append(tc_outputItems)
	part_conf_values_var.append(tc_values_var)
	
csv_part_conf_values = copy.deepcopy(part_conf_values) #copy for orginal csv outputs

if len(part_conf_values)==0 or len(part_conf_values[0])==0:
	print tcolor.FAIL + 'Error: no values read' + tcolor.ENDC
	sys.exit(-1)

if sum(len(x) for x in part_conf_values)!=sum(len(x) for x in part_conf_outputItems):
	print tcolor.FAIL + 'Sry you have ' +str(sum(len(x) for x in part_conf_values))+' data items but '+str(sum(len(x) for x in part_conf_outputItems))+' output item informations -> abort' + tcolor.ENDC
	sys.exit(-1)
	
#calc cycles/outputitems
for i in xrange(0,len(part_conf_values)):
	for j in xrange(0,len(part_conf_values[i])):
		if (part_conf_outputItems[i][j]==0):
			print tcolor.WARN + 'Warning in cycles/outputitem: \"' + partitioners_name[i] + '\" in file \"' + allConfigurations[j] + '\" value is ' + str(part_conf_outputItems[i][j])  + tcolor.ENDC
		else:
			part_conf_values[i][j] = float(part_conf_values[i][j]) / float(part_conf_outputItems[i][j])
			part_conf_values_var[i][j] = float(part_conf_values_var[i][j]) / float(part_conf_outputItems[i][j])
			
#calc speedup
speedup_part_conf_values = copy.deepcopy(part_conf_values) #copy list
speedup_part_conf_values_var = copy.deepcopy(part_conf_values_var) #copy list
for i in reversed(xrange(0,len(speedup_part_conf_values))):
	for j in xrange(0,len(speedup_part_conf_values[i])):
		if (speedup_part_conf_values[i][j]==0):
			print tcolor.WARN + 'Warning in speedup calc: \"' + partitioners_name[i] + '\" in file \"' + allConfigurations[j] + '\" value is ' + str(speedup_part_conf_values[i][j]) + tcolor.ENDC
		else:
			speedup_part_conf_values[i][j] = float(speedup_part_conf_values[0][j])/(float(speedup_part_conf_values[i][j]))
		if (speedup_part_conf_values_var[i][j]!=0):
			speedup_part_conf_values_var[i][j] = float(speedup_part_conf_values_var[0][j])/(float(speedup_part_conf_values_var[i][j]))
	

###graph plotting begin###
ind = np.arange(len(allConfigurations))   # the x locations for the groups
fig = plt.figure()
if not len(title)==0:
	fig.suptitle(title)
ax = fig.add_subplot(111)
width = 0.2 #bar size
bars = []
xPosition = ind;
font_size = 8;

# font
mpl.rcParams['ps.useafm'] = True
#mpl.rcParams['pdf.use14corefonts'] = True
#mpl.rcParams['text.usetex'] = True
mpl.rcParams['pdf.fonttype'] = 42
mpl.rcParams['ps.fonttype'] = 42
mpl.rcParams['pdf.compression'] = 9

#colors
Color = namedtuple("Color", "R G B")
colors = [Color(245./255.,159./255.,0.),Color(121./255.,16./255.,17./255.),Color(182./255.,200./255.,40./255.),Color(0.,98./255.,154./255.),Color(127./255.,13./255.,93./255.),Color(0.,117./255.,86./255.)]

if 'speedup' in active_options:
	# omit 1 core result
	speedup_part_conf_values = speedup_part_conf_values[1:]
	print_partitioners_name = partitioners_name[1:]
	print_values = speedup_part_conf_values
	print_values_var = speedup_part_conf_values_var[1:]
else: 
	print_values = part_conf_values
	print_partitioners_name = partitioners_name
	print_values_var = part_conf_values_var
	
for i,values in enumerate(print_values):
	if 'errorbars' in active_options:
		print "errorbars: "+str(print_values_var[i])
		bars.append(ax.bar(xPosition, values,width,color=colors[i],yerr=print_values_var[i]))
	else:
		bars.append(ax.bar(xPosition, values,width,color=colors[i]))
	xPosition = xPosition+width

#use max free space
box = ax.get_position()
if len(title)==0:
	ax.set_position([box.x0-0.07, box.y0+0.14, box.width*1.21, box.height*0.88])
else:
	ax.set_position([box.x0-0.05, box.y0+0.13, box.width*1.15, box.height*0.85])
	

ax.legend(bars,print_partitioners_name, bbox_to_anchor=(0, 1.02, 1, 1.04), loc=3, ncol=len(partitioners), mode="expand", borderaxespad=0.,prop={'size':10})

#x axis magic
ax.set_xlim(xmin=-width,xmax=len(ind)-1+width*len(partitioners))
if len(ind)%2==0:
	ax.set_xticks(ind+width*len(partitioners)/2-width/2)
else:
	ax.set_xticks(ind+width*len(partitioners)/2)
xtickNames = ax.set_xticklabels(allConfigurations)
plt.setp(xtickNames, rotation=90)
if 'grid' in active_options:
	ax.set_axisbelow(True)
	ax.yaxis.grid()
if 'speedup' in active_options:
	plt.ylabel('speedup')
else:
	plt.ylabel('cycles')
	
plt.rcParams.update({'font.size': 10})
# horizontal line y=1
plt.axhline(y=1, color='black')

plt.savefig(outfile, format='PNG')
plt.savefig(os.path.basename(configfile).replace('.def','.pdf'), format='PDF', bbox_inches='tight')

#Writing all csv data
with open(outfile_csv, 'wb') as f:
	writer = csv.writer(f)
	writer.writerow(['Name','OutputItems']+allConfigurations)
	writer.writerows([['{0}'.format(x),y]+z for x,y,z in zip(partitioners_name, part_conf_outputItems , csv_part_conf_values)])
