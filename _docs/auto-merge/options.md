---
title: More Options
permalink: /docs/options/
---

### Log Levels

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