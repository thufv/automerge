---
title: Artifact Info
permalink: /docs/artifact/
---

### Download

Download [latest version](https://github.com/thufv/automerge/archive/artifact.zip),
or clone the [repository](https://github.com/thufv/automerge/tree/artifact).

Download [OOPSLA'18 AEC version](https://drive.google.com/file/d/16ovkfdX6bg993AT5_WybhpHKFy8UKJDA/view){:target="_blank"}.

### Folder Structure

After extraction, you will see the following folders:

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
```