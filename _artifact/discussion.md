---
title: Discussion
permalink: /docs/discussion/
---

### Does the artifact support the paper?

Yes, mostly.
The two evaluations stated in Section 6.2 and Section 6.3 of the paper are capable of 
being reproduced by this artifact.
Results depicted in Table 3 are available in `outputs/exp1.csv`, 
the numbers in Fig. 9 come from `outputs/exp2.csv`,
and the results in Fig. 8 derives from `output/exp3.txt`.

Two things that are difficult and time-consuming for the readers to reproduce 
are excluded in this artifact.

#### 1. Merge Scenarios Extraction

Section 6.1 gives a detailed explanation of how merge scenarios are extracted.
Extracting the source code of several commits is time-consuming 
when we handle a large number of commits in a large project.
To determine merge scenarios in which conflicts present, we have to perform structured merging 
(with the tool `JDime`) on each merge scenario for each project, 
which also takes a large amount of time.
We decide to directly provide these 95 extracted merge scenarios as benchmarks so that 
readers can reproduce the key experiments easily.

For details, visit
<a href="{{ '/docs/ms-extract' | prepend: site.baseurl }}">Merge Scenario Extraction</a>.
In the reproducing package,
we also provide a sample script `commits.py` for extracting merge scenarios.

#### 2. Analysis on Failure Cases

Section 6.2 analyzes why a few cases failed.
We came up to the conclusions described in the paper by manually observing the cases 
and the output logs.
Therefore, it is difficult for us to provide some programs such that any reader is able to 
reproduce. 
Instead, please read the explanation in the paper if you have any interest.
