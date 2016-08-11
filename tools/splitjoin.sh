#!/usr/bin/env bash

FORCE_SPLITTER="1 10"
FORCE_JOINER="1 10"
BUILD_DIR=splitjoin/

make streamit_build

run() {
	echo start: forcesplitter=$1 forcejoiner=$2
	nice make STR_BUILD_DIR=$BUILD_DIR WHITELIST=tests/Sender80211.str STREAMIT_FLAGS="-xml=config/mpsoc_1x1x1.xml -part=greedy:sim -maxmult=1 -forcesplitter=$1 -forcejoiner=$2" perf_sj
	echo finish: forcesplitter=$1 forcejoiner=$2
}

for split in $FORCE_SPLITTER; do
	for join in $FORCE_JOINER; do
		run $split $join &
	done
done

# wait for threads to finish
wait
