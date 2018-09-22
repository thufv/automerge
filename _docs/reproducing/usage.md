---
title: Usage
permalink: /docs/usage/
---

[Artifact Download](https://drive.google.com/file/d/16ovkfdX6bg993AT5_WybhpHKFy8UKJDA/view)

This gives a brief introduction on how to use `AutoMerge`, provided as a .jar file in `bin/`.
`AutoMerge` is built on the top of `JDime` (https://github.com/se-passau/jdime).
`JDime` is a state-of-the-art structured merge tool, together with unstructured support.
Our Version Space Algebra (VSA) technique is realized as an additional functionality over `JDime`. 
Only for cases that cannot be handled by `JDime`, our approach is activated.

## Basic Usage

In `bin/` folder:

```
java -jar AutoMerge.jar [options...] <left> <base> <right>
```

Users must provide `<left>`, `<base>`, and `<right>` as the input, i.e. a three-way merge scenario.
It is also possible to neglect the 2nd argument `<base>`, which means a two-way merge scenario.
The arguments should be paths of either a file or a folder.

## Synthesis Options

To benefit from our VSA technique, always choose the structured merge mode `-m structured` 
and enable the synthesis option `-S`.

In the experiment, we need to check if our resolution is expected.
For this purpose, specify option `-e <expected>` with the path of the expected version.
The log `Synthesis: Searched total steps: ...` shows the number of search steps we need.
A typical usage is like:
```
java -jar AutoMerge.jar -e expected/ -o output/ -m structured -log info -f -S left/ base/ right/
```

If `-e <expected>` is not specified, then our tool prints the recommended resolutions.
Use `-K <num>` to restrict the number of resolutions presented, default 32.
A typical usage is like:
```
java -jar AutoMerge.jar -o output/ -m structured -log info -f -S left/ base/ right/
```

Other interesting options are:
```
-M1,--mapper-1 <on|off>   Enable/disable mapper 1, default on.
-M2,--mapper-2 <on|off>   Enable/disable mapper 2, default off.
-noR,--no-ranking         Disable ranking, default enabled.
```
These options are set for the experiment shown in Section 6.3.

## Log Levels

The recommended log level is `INFO`, which shows useful information on synthesis results.
Set log level with option `-log <level>`, possible levels are 
(from the least output to the most output):

- `OFF`: No logs.
- `SEVERE`: Error messages only.
- `WARNING`: Warning messages.
- `INFO`: Useful execution results.
- `CONFIG`: Command line configuration information.
- `FINE`, `FINER`, `FINEST`: Debug information in different levels.

A colorful logger is provided. Specify
`de.fosd.jdime.handlers=de.fosd.jdime.util.ColorConsoleHandler`
in `bin/JDimeLogging.properties`.
However, to reproduce our experiments, please keep the default logger:
`de.fosd.jdime.handlers=java.util.logging.ConsoleHandler`.

Unmentioned options are inherited from `JDime`, 
type `java -jar AutoMerge.jar` for help information.
