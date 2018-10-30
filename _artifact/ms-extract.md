---
title: Merge Scenario Extraction
permalink: /docs/ms-extract/
---

### Merge Scenario

In the context of three-way merge,
the two versions to be merged are called the _left_ version and the _right_ version,
and their common ancestor is called the _base_ version.
The base, left and right versions form a _merge scenario_.
For every merge scenario, we let the actually performed one by the developers as the 
"ground truth" and name it the _expected version_.

For a git repository, every merge commit contains merging,
and the commit itself is the actual merged result.
Therefore, for every merge commit,
we can extract a merge scenario and regard itself as the expected version.

### Steps of Extraction

1. Select the repositories that you expect to evaluate on.
2. For each repository, analyze the commit histories and identify all merge commits.
3. For each merge commit, treat the base commit as the base version,
and two parent commits as the left and right versions.
In fact, these three versions form a merge scenario.
Additionally, the merge commit itself is marked as the expected version
with respect to the merge scenario.
4. Not all of the merge commits are "interesting":
some of them can be fully merged by structured merge
and thus it is unnecessary to apply our conflict resolution approach on these merge scenarios.
Instead, we only concentrate on merge commits that cannot be fully merged by `JDime`,
say at least one conflict exhibits.

### Usage of Script

In some working directory `dir/`, first create a new subdirectory `projects/`, 
and move the repositories into `projects/`. 
Each repository must be a folder and we access the repository by the name of the folder.

Then copy the [script](https://github.com/thufv/automerge/blob/artifact/commits.py){:target="_blank"}
into directory `dir/`.
In `dir/`, type `./commits repos...` to extract commits of 
a list of repositories (providing their folder names as the CLI arguments).

The extracted commits will be saved into `dir/commits/`. The folder structure is like the following:

```
commits/
    repo1/
        commit1_hash/
            base/
            left/
            right/
            expected/
        ... (other commits)
    ... (other repos)
```

The script itself is not complicated and you may customize it to meet your needs.
When using the script, please note that:
- Non-Java files will be excluded in the commit, as `JDime` only support Java.
- The script only extracts a bunch of commits. If you want to perform a merge by `AutoMerge`,
please use the scripts provided in this package.
Also, the last step of extraction, i.e. filter "interesting" commits, cannot be done by the script.
In fact, you may launch the entire experiment simply on these extracted commits,
and then you analyze the results and you could conclude that 
how many of the merge scenarios were not solved by only the structured merge.