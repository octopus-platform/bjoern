# bjoern-radare: A Radare to Graph Database Exporter

bjoern-radare allows analysis results of the Radare reverse
engineering framework (http://www.radare.org/) to be imported into
graph databases for subsequent analysis via traversals. It thus
exposes results of function, argument, and variable detection, control
flow graphs, including the corresponding instructions and basic
blocks, as well as data, code and call cross references.

## Dependencies

Bjoern-radare is written for Java 8. Apart from that, only radare2 and
its bindings are required. As radare2 is subject to rapid changes,
forks of radare2 compatible with bjoern-radare are mainted here:

* Radare2: https://github.com/fabsx00/radare2-bindings
* Radare2-bindings: https://github.com/fabsx00/radare2-bindings
* Orientdb 2.1.5 Community Edition

## Installing radare2

Bjoern-radare requires shared objects from radare2 and its bindings to
be available in the `lib` directory. You can build these for your
platform by following the instructions here:

http://www.radare.org/r/down.html

When building radare2-bindings, make sure that the compiler has access
to Java 8's `jni.h` and `jni_md.h` by specifying CFLAGS accordingly.

Alternatively, pre-built versions for Linux on AMD64 can be found
here:

http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/lib-linux-amd64.tar.gz

## Installating bjoern-radare

Installation of bjoern-radare follows three steps: first, the native
radare libaries are placed in the `lib` directory. Second, java
archives (jars) are placed into the `jar` directory. Finally,
bjoern-radare is built.

### Installing native libraries

Assuming that you have installed radare2 manually into '/usr/local'
and extracted bjoern-radare into `$BJOERN_RADARE`, copy all files in

	/usr/local/share/radare2/java/jni/lib

to the `$BJOERN_RADARE/lib` directory. Alternatively, on Linux AMD64,
simply place lib-linux-amd64.tar.gz into `$BJOERN_RADARE` and extract
it.

### Installing required jars

Proceed to obtain the tarball from:

http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz

and extract it in `$BJOERN_RADARE/jar`..

### Building bjoern-radare

Next, build bjoern-radare using ant:

	ant
	ant orientdbImporter

This will create an executable JAR in `$BJOERN_RADARE/bin/bjoern-radare.jar`.

### Installing orientdb

Finally, place orientdb-community-2.1.5 into the `$BJOERN_RADARE` directory.

You can run the jar using the script `bjoern-radare.sh`
