# AutoMerge  -  Resolving Conflicts via Version Space Algebra

This project is built on the top of [JDime](https://github.com/se-passau/jdime).

For technical details of our approach, please read our paper:

_Fengmin Zhu and Fei He. 2018. Conflict Resolution for Structured Merge via Version Space Algebra. Proc. ACM Program. Lang. 2, OOPSLA, Article 166 (November 2018)._
(will be published in Nov.)

## Background

Software merge is a central task of version control system-guided development:
two developers create their own versions by deriving it from a common ancestor version; then these two versions evolve independently by either adding new functionalities or fixing previous issues; finally these two versions are merged again.
In a [three-way merge](https://en.wikipedia.org/wiki/Merge_(version_control)){:target="_blank"}
scenario, 
the two versions to be merged are called the _left_ version and the _right_ version,
and their common ancestor is called the _base_ version.
The base, left and right versions form a _three-way merge scenario_.
If the base version is unavailable, then it is called a _two-way merge scenario_.
`AutoMerge` is mainly focus on three-way merge, but it also supports two-way merge.

When concurrent changes contradict each other in the _left_ and _right_ versions,
conflicts are reported according to the basic rules of three-way merge.
Existing merge tools do not attempt to resolve conflicts and leave the problems for developers
to manually handle them.
AutoMerge is designed to provide the developer with a variety of useful candidate resolutions,
which we believe is valuable and practical in real-world software development.

## System Requirements

* JDK8 + JavaFX
* [git](http://git-scm.com/)
* [libgit2](https://libgit2.github.com/)
* [gradle](https://gradle.org)

Installing libgit2 on Linux:
- Debian/Ubuntu: `apt-get install git libgit2-dev`
- Redhat/Fedora: `dnf install git libgit2`
- Suse/OpenSuse: `zypper install git libgit2`
- FreeBSD: `pkg install git openjfx8-devel libgit2`

## Build & Test

The simplest way to build and run AutoMerge is 

```bash
gradle run --args='<options>'
```

Without any options, say `gradle run`, AutoMerge prints the usage.
To normally launch AutoMerge,
you need to combine the CLI options and arguments as a string `'<options>'` 
and pass it as the argument for the gradle option `--args`, i.e. `--args='<options>'`.

To launch a demo, simply type `gradle demo` and have a look at the output logs.
Alternatively, you may first build by `gradle installDist` and then execute the helper script
to perform a merge by providing the three-way merge scenario (base, left, right)
and the output path. The expected result is optional:

```bash
./test.sh <left> <base> <right> <output> [<expected>]
```

## Packing

For further usage of AutoMerge, we recommend you to generate a standalone jar by typing
`gradle pack`. You will find the standalone jar in `build/pack/AutoMerge.jar`.
Change directory to `build/pack` and execute the jar with `java -jar AutoMerge.jar ...`.

## Basic Usage

```
java -jar AutoMerge.jar [options...] <left> [<base>] <right>
```

Users must provide `<left>`, `<base>`, and `<right>` as the input, i.e. a three-way merge scenario.
It is also possible to neglect the 2nd argument `<base>`, which means a two-way merge scenario.
The three arguments should be either three files or three folders.

### Synthesis Options

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
### Mapper & Ranking Options

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

These options are set for the experiment shown in Section 6.3 of our paper.

## Further Information

For further information, please visit our [webpage](https://thufv.github.io/automerge).
We also provide an [artifact](https://github.com/thufv/automerge/tree/artifact)
that reproduces the experiment mentioned in our paper.