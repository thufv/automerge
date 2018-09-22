---
title: Steps
permalink: /docs/steps/
---

## Folder Structure

```
automerge-artifact/		# project root
	commits/			# extracted commits (i.e. merge scenarios)
	bin/				# executable files
		AutoMerge.jar 				# our tool
		JDime.properties 			# JDime properties
		JDimeLogging.properties  	# logger properties
		tmp.java        			# temporary file
		test.sh 					# sample launcher
	outputs/			# output files
		release/		# evaluation results on our machine
	sample/ 			# a little merge scenario as a sample

	README.md 			# reproducing guide
	run-all.sh  		# top level start script
	options.py  		# configurations for experiments
	run-conflicts.py 	# experiments launcher
	stat-conflicts.py 	# log processer and raw results generator
	gen-tables.py 		# final results generator
```

## System Requirements

- Unix-like OS, Ubuntu 16.04 recommended.
- Java version: JDK8. (Java 9 may be incompatible on some machines, so please pick Java 8.)
- Python2.
- `libgit2` (https://libgit2.github.com/), required by the original `JDime` tool.

Please use the following commands to install `libgit2` if necessary:

* Debian/Ubuntu: `apt-get install git libgit2-dev`
* Redhat/Fedora: `dnf install git libgit2`
* Suse/OpenSuse: `zypper install git libgit2`
* FreeBSD: `pkg install git openjfx8-devel libgit2`

## Testing

Before reproducing, we recommend you to type the following commands

```
cd bin/
./test.sh
```

to check if `AutoMerge` normally executes.
If you see the log `SUCCESS: Synthesis: FOUND`, then it works fine.
Two main reasons that causes troubles:
- JDK version is incompatible. Use JDK8.
- `libgit2` is not correctly installed and configured. See the installation commands above.

Also, you may use this script to try `AutoMerge` with your own merge scenario:

```
./test.sh <left> <base> <right> [<expected>]
```

where the arguments `<left>`, `<base>` and `<right>` can be either a folder or a file,
and `<expected>` is optional.
For more information on how to use `AutoMerge`, read `AutoMerge.md`.

## Reproducing Steps

We have put up all the steps together in a shell script `run-all.sh`, simply run

```
./run-all.sh
```

and have a cup of coffee.
The total runtime is around one hour on our machine, i7-7700 CPU (3.60 GHz) with 16 GB memory,
running Ubuntu 16.04.

The script actually does the following magics:
1. `./run-conflicts.py`: launch experiments with 4 different configurations 
(see Page 17, Section 6.3 of our paper).
2. `./run-conflicts.py PS -PS`: launch an extra experiment to measure the size of program space 
(see Page 15, Table 3, Column P.S.).
3. `./stat-conflicts.py PS`: process .log files (the output of our tool), 
analyze them and create .json files as raw results.
4. `./gen-tables.py`: read .json files (from Step 3) and generate .csv files as final results.

The two generated .csv files describe the results presented in our paper:
- `outputs/exp1.csv` -> Page 15, Table 3
- `outputs/exp2.csv` -> Page 18, Fig. 8

In `outputs/release`, we provide the expected evaluation results.
Since we have improved the measurement of the program space (column name `P.S.`) 
listed in `outputs/release/exp1.csv` is different from that one in the paper. 
New results will be updated in the 2nd round paper submission.

## Does the artifact support the paper?

Yes, mostly.
The two evaluations stated in Section 6.2 and Section 6.3 in the paper are capable of 
being reproduced by this artifact.
Results depicted in Table 3 are available in `outputs/exp1.csv`, 
and the numbers in Fig. 8 come from `outputs/exp2.csv`.

Two things that are difficult and time-consuming for the readers to reproduce 
are excluded in this artifact.

### 1. Merge Scenarios Extraction

Section 6.1 gives a detailed explanation of how merge scenarios are extracted.
Extracting the source code of several commits is time-consuming 
when we handle a large number of commits in a large project.
To determine merge scenarios in which conflicts present, we have to perform structured merging 
(with the tool `JDime`) on each merge scenario for each project, 
which also takes a large amount of time.
We decide to directly provide these 95 extracted merge scenarios as benchmarks so that 
readers can reproduce the key experiments easily.

### 2. Analysis on Failure Cases

Section 6.2 analyzes why a few cases failed.
We came up to the conclusions described in the paper by manually observing the cases 
and the output logs.
Therefore, it is difficult for us to provide some programs such that any reader is able to 
reproduce. 
Instead, please read the explanation in the paper if you have any interest.
