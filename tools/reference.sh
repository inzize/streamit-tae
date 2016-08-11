#!/bin/sh
filelist=`ls *.str`
#filelist=Audiobeam.str
for sourcefile in $filelist
do
	source_basename=`basename $sourcefile .str` 
	echo $source_basename
	working_dir=generated_src/$source_basename/java
	mkdir -p $working_dir
	java -ea -classpath ../../../coreva_toolchain/compiler/streamit/bin/streamit.jar streamit.frontend.ToJava --library --output $working_dir/$source_basename.java $source_basename.str
	javac -classpath ../../../coreva_toolchain/compiler/streamit/bin/streamit.jar $working_dir/$source_basename.java
	cd $working_dir
	timeout 20 java -classpath ../../../../../../coreva_toolchain/compiler/streamit/bin/streamit.jar:. $source_basename | head -1000 > ../../../ref_outputs/$source_basename.ref_output
	cd -
done
