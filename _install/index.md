---
title: Introduction
permalink: /docs/home/
redirect_from: /docs/index.html
---

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
`AutoMerge` is designed to provide the developer with a variety of useful candidate resolutions,
which we believe is valuable and practical in real-world software development.

`AutoMerge` is built on the top of `JDime`, a state-of-the-art structured merge
[tool](https://github.com/se-passau/jdime){:target="_blank"}.
Our Version Space Algebra (VSA) technique is realized as an additional functionality over `JDime`.
When structured merging reports conflicts in a three-way merge scenario, 
our approach is then activated.