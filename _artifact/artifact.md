---
title: Package Info
permalink: /docs/artifact/
---

### Reproducing Package

This package is provided for you to reproduce the experiments described in our paper:

_Fengmin Zhu and Fei He. 2018. Conflict Resolution for Structured Merge via Version Space Algebra. Proc. ACM Program. Lang. 2, OOPSLA, Article 166 (November 2018), 25 pages._
[Download via DOI](https://doi.org/10.1145/3276536){:target="_blank"}

The artifact submitted to OOPSLA'18 AEC has been evaluated as _functional_,
and you can download this
[AEC version](https://drive.google.com/file/d/16ovkfdX6bg993AT5_WybhpHKFy8UKJDA/view){:target="_blank"}.

The latest artifact: 
either download the [zip archive](https://github.com/thufv/automerge/archive/artifact.zip),
or clone our [repository](https://github.com/thufv/automerge/tree/artifact){:target="_blank"} 
and checkout `artifact` branch.

### Folder Structure

After extracting the archive, you will see the following folders:

```
automerge-artifact/                # project root
    commits/                       # extracted commits (i.e. merge scenarios)
    bin/                           # executable files
        AutoMerge.jar              # our tool
        JDime.properties           # JDime properties
        JDimeLogging.properties    # logger properties
        tmp.java                   # temporary file
        test.sh                    # sample launcher
    outputs/                       # output files
        release/                   # evaluation results on our machine
    sample/                        # a little merge scenario as a sample

    README.md                      # reproducing guide
    run-all.sh                     # top level start script
    options.py                     # configurations for experiments
    run-conflicts.py               # experiments launcher
    stat-conflicts.py              # log processer and raw results generator
    gen-tables.py                  # final results generator
    commits.py                     # commits extractor
```