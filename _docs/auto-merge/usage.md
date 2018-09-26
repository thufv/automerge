---
title: Basic Usage
permalink: /docs/usage/
---

### Two-way Merge v.s. Three-way Merge

In a [three-way merge](https://en.wikipedia.org/wiki/Merge_(version_control)){:target="_blank"}
scenario, 
the two versions to be merged are called the _left_ version and the _right_ version,
and their common ancestor is called the _base_ version.
The base, left and right versions form a _three-way merge scenario_.
If the base version is unavailable, then it is called a _two-way merge scenario_.
`AutoMerge` is mainly focus on three-way merge, but it also supports two-way merge.

### Usage

```
java -jar AutoMerge.jar [options...] <left> [<base>] <right>
```

Users must provide `<left>`, `<base>`, and `<right>` as the input, i.e. a three-way merge scenario.
It is also possible to neglect the 2nd argument `<base>`, which means a two-way merge scenario.
The three arguments should be either three files or three folders.

#### Synthesis Options

To benefit from our VSA-based technique, always choose the structured merge mode `-m structured` 
and enable the synthesis option `-S`.

In the experiment, we need to check if our resolution is expected.
For this purpose, specify option `-e <expected>` with the path of the expected version.
Note that if the arguments are files/folders, then the expected version must also be a file/folder.

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
#### Mapper & Ranking Options

When constructing the VSA for candidate resolutions, 
we are concern about which arguments nodes are distinguishable and which are indistinguishable.
We introduce _mappers_ to capture it and we have three pre-defined mappers:

- direct mapper,
- block mapper, and
- expression mapper.

The direct mapper is the origin of other mappers and is thus always enabled.
The other two mappers can be enabled/disabled with options.

The generated VSA contains a possibly very large set of programs and 
enumerating them is apparently impractical.
Developers expect the target result to be figured out as early as possible.
Thus, we define a _ranking function_ which assigns a priority for every candidate resolution and
the resolutions are enumerated from the top-ranked to the bottom-ranked.
Ranking is enabled by default and can be disabled with options.

Options for mappers and ranking:
- `-M1,--mapper-1 <on|off>`: Enable/disable mapper 1 (block mapper), default on.
- `-M2,--mapper-2 <on|off>`: Enable/disable mapper 2 (expression mapper), default off.
- `-noR,--no-ranking`: Disable ranking, default enabled.

These options are set for the experiment shown in Section 6.3.