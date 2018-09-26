---
title: Steps
permalink: /docs/steps/
---

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

### Script Usages

The shell script `run-all.sh` already covers all necessary commands for the reproducing steps.
For users who are interested in the details, we briefly introduce the basic usages for each script.

#### 1. run-conflicts.py

This script calls `AutoMerge` to perform the merging and write logs to files.
Options are allowed to specify configurations,
i.e. the CLI options that are passed (as extra options) to `AutoMerge`,
and users must give each configuration a `<label>`,
which is later used to identify results related to this configuration.

Usages:
- `./run-conflicts.py`: 
launch experiments with the default four configurations (listed in `options.py`).
- `./run-conflicts.py <label> [<option>...]`: 
launch an experiment with the customized configuration `<option>...`
(if empty, the default setting of `AutoMerge` is used) labeled with `<label>`.

Outputs:
For each `<label>`, the log file is named as `<label>.log`.

#### 2. stat-conflicts.py

This script processes logs and create intermediate results.
Users are free to specify which logs are processed by the label of the configuration.

Usages:
- `./stat-conflicts.py`: process the logs of default four configurations.
- `./stat-conflicts.py <label>...`:
process the logs of default four configurations plus the ones labeled by arguments `<label>...`.
- `./stat-conflicts.py --only <label>...`:
process the logs of the ones labeled by arguments `<label>...` only.

Outputs:
For each `<label>`, three kinds of files are generated:
- Filtered log files, named as `<label>.filtered.log`.
- Raw result JSON files, named as `<label>.json`.
- Merged AST JSON files, named as `<label>.mergedASTData.json`.

#### 3. gen-tables.py

This script generates the final results by loading the intermediate results.
Users may decide which result needs to be generated.

Usages:
- `./gen-tables.py`: generate results of all three experiments.
- `./gen-tables.py exp1|exp2|exp3`: generate the result of the specified experiment.

Outputs:
- `outputs/exp1.csv` for experiment 1 (if specified).
- `outputs/exp2.csv` for experiment 2 (if specified).
- `outputs/exp3.txt` for experiment 3 (if specified).
