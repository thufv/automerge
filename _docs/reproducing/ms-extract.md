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
4. Not all of the merge commits are interesting.
Some of them can be fully merged by structured merge
and thus it is unnecessary to apply our conflict resolution approach on these merge scenarios.
Instead, we only concentrate on merge commits that cannot be fully merged by `JDime`,
say at least one conflict exhibits.

### Usage of Script