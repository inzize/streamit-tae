#!/usr/bin/env python

#def file options: grid, speedup, errorbars

import matplotlib as mpl
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

class tcolor:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARN = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'

if len(sys.argv)!=3:
	print "You need 2 parameters (csv directory + config)"
	sys.exit(-1)
	
#check parameters
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

#load config file
config = open(configfile,'r')
configlines = config.readlines()

#define output files
outfile = os.path.basename(configfile).replace('.def','.png')
outfile_csv = os.path.basename(configfile).replace('.def','.csv')
os.chdir(rootdir)
print tcolor.OKGREEN + 'cd to ' + rootdir + tcolor.ENDC

partitioners = []
partitioners_name = []

#parse config file
active_options = []
title = ""
for configline in configlines:
	if '=' in configline:
		data = configline.split('=')
		if data[0] == 'title':
			title = data[1]
		elif len(data)==2 and data[1].startswith('1'):
			print "active option: "+data[0]
			active_options.append(data[0].strip().lower())
	else: 
		if not os.path.isdir(configline.split(':')[0].rstrip()):
			print configline.split(':')[0].rstrip() + ' partitioner not found -> skipping'
			continue
		partitioners.append(configline.split(':')[0].rstrip())
		partitioners_name.append((configline.split(':')[1]).strip())
		
if len(partitioners) == 0:
	print tcolor.FAIL + 'No partitioners to plot, aborting' + tcolor.ENDC
	sys.exit(-1)
allTC 	 = os.listdir(partitioners[0]) #all testcases

allTC.sort()

#read files / calc avg values
part_tc_values_var = [] #for each partitioner list of sample variance values
part_tc_values = [] #for each partitioner list of avg values
part_tc_outputItems = []
for parts in partitioners:
	tc_values_var = []
	tc_values = [] #all avg values for the actual partitioner
	tc_outputItems = []
	for tcs in allTC:
		file = parts+"/"+tcs
		if not os.path.isdir(file):
			print tcolor.WARN + file + " doesn't exist -> skipping" + tcolor.ENDC
			tc_values.append(0)
			continue
		bench_files = glob.glob(file+'/sim/trace/sim*.csv')
		print 'reading ' + file + ' (found ' + str(len(bench_files)) + ' csv files)'
		bench_avg   = 0; # highest avg value for all cyc files
		raw_values = []
		for bench_file in bench_files:
			f = open(bench_file,'r')
			line = f.readline()
			while not line.endswith(';10\n'):#drop all before first steady
				line = f.readline()
			value = float(line.split(';')[0])
			line = f.readline();
			while not line.endswith(";10\n"):#read one complete steady state
				line = f.readline()
			lastClock = int(line.split(';')[0])
			value = float(line.split(';')[0]) - value
			raw_values.append(float(value))
			avg=value
			min=value
			max=value
			num_lines=1
			for line in f:
				if not line.endswith(";10\n"):
					continue
				value = int(line.split(';')[0]) - lastClock
				lastClock = int(line.split(';')[0])
				avg += value
				if (value<min):
					min=value
				if (value>max):
					max=value
				num_lines+=1
				raw_values.append(value)
			avg/=num_lines
			if avg>bench_avg:
				bench_avg = avg
			if (max-min>avg*0.05):
				print tcolor.WARN + 'Warn: range in ' + bench_file + ' is ' + str(max-min) + tcolor.ENDC
		file = parts + '/' + tcs + "/PerfEstimate.txt"
		if not os.path.isfile(file):
			print tcolor.WARN + 'Warning: ' + file + ' not found skipping' + tcolor.ENDC
			tc_values.append(0)
			continue
		f = open(file,'r')
		for line in f: #skip all lines (go to last line)
			pass
		if not 'outputItems' in line:
			print tcolor.FAIL + 'Warning: mailformed txt (' + file + ')' + tcolor.ENDC
			sys.exit(-1)
		outputItems = line[line.rfind('outputItems')+12:line.rfind(')')].split(",")[0];
		var = 0
		for rawValues in raw_values:
			var += (rawValues - bench_avg) * (rawValues - bench_avg)
		var /= len(raw_values)
		tc_values_var.append(var);
		tc_values.append(bench_avg)
		tc_outputItems.append(outputItems)
	part_tc_values.append(tc_values)
	part_tc_outputItems.append(tc_outputItems)
	part_tc_values_var.append(tc_values_var)
csv_part_tc_values = copy.deepcopy(part_tc_values) #copy for orginal csv outputs

if len(part_tc_values)==0 or len(part_tc_values[0])==0:
	print tcolor.FAIL + 'Error: no values read' + tcolor.ENDC
	sys.exit(-1)

if sum(len(x) for x in part_tc_values)!=sum(len(x) for x in part_tc_outputItems):
	print tcolor.FAIL + 'Sry you have ' +str(sum(len(x) for x in part_tc_values))+' data items but '+str(sum(len(x) for x in part_tc_outputItems))+' output item informations -> abort' + tcolor.ENDC
	sys.exit(-1)
	
#calc cycles/outputitems
for i in xrange(0,len(part_tc_values)):
	for j in xrange(0,len(part_tc_values[i])):
		if (part_tc_outputItems[i][j]==0):
			print tcolor.WARN + 'Warning in cycles/outputitem: \"' + partitioners_name[i] + '\" in file \"' + allTC[j] + '\" value is ' + str(part_tc_outputItems[i][j])  + tcolor.ENDC
		else:
			part_tc_values[i][j] = float(part_tc_values[i][j]) / float(part_tc_outputItems[i][j])
			part_tc_values_var[i][j] = float(part_tc_values_var[i][j]) / float(part_tc_outputItems[i][j])
			
#calc speedup
speedup_part_tc_values = copy.deepcopy(part_tc_values) #copy list
speedup_part_tc_values_var = copy.deepcopy(part_tc_values_var) #copy list
for i in reversed(xrange(0,len(speedup_part_tc_values))):
	for j in xrange(0,len(speedup_part_tc_values[i])):
		if (speedup_part_tc_values[i][j]==0):
			print tcolor.WARN + 'Warning in speedup calc: \"' + partitioners_name[i] + '\" in file \"' + allTC[j] + '\" value is ' + str(speedup_part_tc_values[i][j]) + tcolor.ENDC
		else:
			speedup_part_tc_values[i][j] = float(speedup_part_tc_values[0][j])/(float(speedup_part_tc_values[i][j]))
		if (speedup_part_tc_values_var[i][j]!=0):
			speedup_part_tc_values_var[i][j] = float(speedup_part_tc_values_var[0][j])/(float(speedup_part_tc_values_var[i][j]))
	

#create graph
ind = np.arange(len(allTC))   # the x locations for the groups
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
	speedup_part_tc_values = speedup_part_tc_values[1:]
	print_partitioners_name = partitioners_name[1:]
	print_values = speedup_part_tc_values
	print_values_var = speedup_part_tc_values_var[1:]
else: 
	print_values = part_tc_values
	print_partitioners_name = partitioners_name
	print_values_var = part_tc_values_var
	
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
xtickNames = ax.set_xticklabels(allTC)
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
	writer.writerow(['Name','OutputItems']+allTC)
	writer.writerows([['{0}'.format(x),y]+z for x,y,z in zip(partitioners_name, part_tc_outputItems , csv_part_tc_values)])
