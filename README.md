# AutoMerge  -  Resolving Conflicts via Version Space Algebra

This project is built on the top of [JDime](https://github.com/se-passau/jdime).

For technical details of our approach, please read the paper:

_Fengmin Zhu and Fei He. 2018. Conflict Resolution for Structured Merge via Version Space Algebra. Proc. ACM Program. Lang. 2, OOPSLA, Article 166 (November 2018)._
(will be published in Nov.)

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

## Further Information

Please visit our [webpage](https://thufv.github.io/automerge).