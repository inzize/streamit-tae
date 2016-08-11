#!/usr/bin/env python
# -*- coding: utf-8 -*-

# first parameter is a program name e.g. Sender80211
# than this script should run in generated_src
# this script iterates over different program instances, the name scheme is program_instanceNumber e.g. Sender80211_1

# You can also run this script in regtest/#/ with  zero parameters 

import sys
from glob import glob
import re
import os
def parseFile(f, cycle_map):
	filter_nb = None
	for line in f:
		cycle, sep, label = line.strip().partition(";")
		if label == "10":  # 10 is the steady state start marker
			if filter_nb is not None:  # first steady state complete
				break;
			filter_nb = label
		if int(label) > 9 and filter_nb is not None:  # > 9 is reserved for filter ids
			filter_nb = label
		if label == "5" and filter_nb is not None:  # end of work found
			filter_id = int(filter_nb) - 10
			cycle_map[filter_id] = cycle_map.get(filter_id,[]) + [(int(cycle)-int(prev_cycle))]
		prev_cycle = cycle

if __name__ == '__main__':
	if len(sys.argv) == 2:
	# parse files
		cycles = dict()
		numeric_key = lambda key: int(key.rsplit('_',1)[1])
		for progam_inst in sorted(glob("%s_*" % sys.argv[1]), key=numeric_key):
			f = open("%s/sim/trace/sim_cpu_0_0_0.csv" % progam_inst)
			parseFile(f,cycles)
		# print perf data to stdout
		for filter_id in cycles.keys():
			print("%s=%s" % (filter_id, str(cycles[filter_id])[1:-1]))
	else:
		programs = glob('configmpsoc1x1x1greedy*/*')
		programs = [program.split('/')[1] for program in programs]
		for program in programs:
			cycles = dict()
			pattern = re.compile('.*mult(?P<num>\d+).*')
			numeric_key = lambda key: int(pattern.match(key).group('num'))
			for config in sorted(glob('config*'), key=numeric_key):
				csv_file = glob('%s/%s/sim_b*/trace/sim_cpu_0_0_0.csv' % (config,program))
				try:
					f = open(csv_file[0])
				except:
					break
				parseFile(f,cycles)
			print 'Parsing %s done' % program
			ref = cycles.values()[0]
			check = False
			for f_id, c in cycles.iteritems():
				if len(c)!=len(ref):
					check = True
			if check:
				print 'Length mismatch -> skipping'
				continue
			perf_file = open('%s/tests/%s.perf'%(os.environ['COREVA_STREAMIT'],program),'w')
			for f_id, c in cycles.iteritems():
				perf_file.write('%s=%s\n'%(f_id,str(c)[1:-1]))
			print 'Write done'
