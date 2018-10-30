---
title: FAQ
permalink: /docs/faq/
---

This section only gives a few common issues that users may encounter.
We would like to thank the anonymous reviewers of the AEC track for useful feedbacks.

**Q:** JDK version is incompatible.

**A:** Use JDK8.
<br/><br/>

**Q:** Library `libgit2` not found.

**A:** It should be easy to install `libgit2` on Linux via the system package manager.
On Mac, you may use `homebrew` to install it: `brew install libgit2`.
On Windows, you may have to compile and install it by yourself: 
[libgit2](https://github.com/libgit2/libgit2){:target="_blank"}.
<br/><br/>

**Q:** On Ubuntu 17.10, I encounter the following exception:
```
SEVERE: Uncaught exception.
java.lang.NoClassDefFoundError: javafx/util/Pair
        at de.fosd.jdime.merge.OrderedMerge.split(OrderedMerge.java:95)
        at de.fosd.jdime.merge.OrderedMerge.merge3(OrderedMerge.java:179)
        ...
```

**A:** You have to install
[openjfx](http://openjdk.java.net/projects/openjfx/getting-started.html){:target="_blank"}.
<br/><br/>