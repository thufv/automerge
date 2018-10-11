#!/usr/bin/env bash

if [ $# == 0 ] ; then 
	./build/install/AutoMerge/bin/AutoMerge -e sample/expected -o sample/output -m structured -log info -f -S sample/left sample/base sample/right
elif [ $# -ge 4 ] ; then
	if [ $# == 4 ] ; then
		./build/install/AutoMerge/bin/AutoMerge -o $4 -m structured -log info -f -S $1 $2 $3
	else
		./build/install/AutoMerge/bin/AutoMerge -e $5 -o $4 -m structured -log info -f -S $1 $2 $3
	fi
else
	echo "Usage: $0 <left> <base> <right> <output> [<expected>]"
	exit 1
fi