bjoern-radare: A Radare to Graph Database Exporter
===================================================

bjoern-radare allows analysis results of the Radare reverse
engineering framework (http://www.radare.org/) to be imported into
graph databases for subsequent analysis via traversals. It thus
exposes results of function, argument, and variable detection, control
flow graphs, including the corresponding instructions and basic
blocks, as well as data, code and call cross references.

Dependencies
------------

Bjoern-radare is written for Java 8. Apart from that, only radare2 and
its bindings are required. As radare2 is subject to rapid changes,
forks of radare2 compatible with bjoern-radare are mainted here:

* Radare2: https://github.com/fabsx00/radare2-bindings
* Radare2-bindings: https://github.com/fabsx00/radare2-bindings

Installing radare2
-------------------

Instructions for installing radare2 and radare2-bindings can be found
here:

http://www.radare.org/r/down.html

When building radare2-bindings, make sure that the compiler has access
to Java 8's `jni.h` and `jni_md.h` by specifying CFLAGS accordingly.

Installating bjoern-radare
---------------------------

Assuming that radare2 and its bindings are installed in /usr/local,
and bjoern-radare is located in $BJOERN_RADARE, copy all files in 

    /usr/local/share/radare2/java/jni/lib

to the `$BJOERN_RADARE/lib` directory, and copy `radare2.jar` from

   /usr/local/share/radare2/java/jni/jar

to `$BJOERN_RADARE/jar`.

TODO: Create an ant build file.

