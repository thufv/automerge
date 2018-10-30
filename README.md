# AutoMerge Artifact

This the reproducing package for the paper

_Fengmin Zhu and Fei He. 2018. Conflict Resolution for Structured Merge via Version Space Algebra. Proc. ACM Program. Lang. 2, OOPSLA, Article 166 (November 2018), 25 pages._
[Download via DOI](https://doi.org/10.1145/3276536)

## Folder Structure

```
automerge-artifact/		# project root
    commits/			# extracted commits (i.e. merge scenarios)
    bin/				# executable files
        AutoMerge.jar 				# our tool
        AutoMerge.properties 			# properties
        AutoMergeLogging.properties  	# logger properties
        tmp.java        			# temporary file
        test.sh 					# sample launcher
    outputs/			# output files
        release/		# evaluation results on our machine
    sample/ 			# a little merge scenario as a sample

    README.md 			# reproducing guide
    run-all.sh  		# top level start script
    options.py  		# configurations for experiments
    run-conflicts.py 	# experiments launcher
    stat-conflicts.py 	# log processor and raw results generator
    gen-tables.py 		# final results generator
    commits.py          # merge commits extractor
```


### System Requirements

- Unix-like OS, Ubuntu 16.04 recommended.
- Java version: JDK8. (Java 9 may be incompatible on some machines, so please pick Java 8.)
- Python2.
- `libgit2` ([Github](https://libgit2.github.com/)), required by the original `JDime` tool.

Please use the following commands to install `libgit2` if necessary:

* Debian/Ubuntu: `apt-get install git libgit2-dev`
* Redhat/Fedora: `dnf install git libgit2`
* Suse/OpenSuse: `zypper install git libgit2`
* FreeBSD: `pkg install git openjfx8-devel libgit2`

### Testing

Before reproducing, we recommend you to type the following commands

```
cd bin/
./test.sh
```

to check if `AutoMerge` normally executes.
If you see the log `SUCCESS: Synthesis: FOUND`, then it works fine.
Otherwise, please refer to <a href="{{ '/docs/faq' | prepend: site.baseurl }}">FAQ</a>
for solutions to common issues.

### Reproducing Steps

We have put up all the steps together in a shell script and reproducing is done via one step:

```
./run-all.sh
```

Then you may have a cup of coffee,
as the total execution time is around 1 hour on our machine 
(i7-7700 CPU (3.60 GHz) with 16 GB memory, running Ubuntu 16.04).

The script actually does the following magics:
1. Launch experiments with the four configurations
(see Page 19, Section 6.3 of the paper).
2. Launch an extra experiment to measure the size of program space
(see Page 17, Table 3, Column P.S.).
3. Process the log files (output of `AutoMerge`),
analyze them and create intermediate results.
4. Load the intermediate results (from Step 3) and generate final results.

The following files containing the final results describe exactly the ones presented in our paper:
- `outputs/exp1.csv` -> Page 17, Table 3
- `outputs/exp2.csv` -> Page 20, Fig. 9
- `outputs/exp3.txt` -> Page 18, Fig. 8

In `outputs/release`, we provide the evaluation results produced by our machine.

## Further Information

Please visit [AutoMerge's website](https://thufv.github.io/automerge/) for

- [More explanation on reproducing](https://thufv.github.io/automerge/docs/steps/)
- [Merge scenario extraction](https://thufv.github.io/automerge/docs/ms-extract/)