# How to install
Install sbt.
http://www.scala-sbt.org/

We also need python and matplotlib to make plots.

And we also need to build libpws

# How to run

The jvm is hardcoded to require 30G physical RAM.
We need at least this much to handle the sizes of the benchmarks.

## Giraffe + squashing
in root dir,
> python benchmarks/giraffe_squashing.py
This will run... for a long time, and produce a .pdf in benchmarks dir.
