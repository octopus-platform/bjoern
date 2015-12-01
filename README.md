# bjoern-radare: A Radare to Graph Database Exporter

bjoern-radare allows analysis results of the Radare reverse
engineering framework (http://www.radare.org/) to be imported into
graph databases for subsequent analysis via traversals. It thus
exposes results of function, argument, and variable detection, control
flow graphs, including the corresponding instructions and basic
blocks, as well as data, code and call cross references.

## Dependencies

* Java 8
* Radare2: http://www.radare.org/r/down.html
* Orientdb 2.1.5 Community Edition

## Installation

First, clone the repository:

	git clone https://github.com/fabsx00/bjoern-radare
	cd bjoern-radare

Next, install orientdb 2.1.5 community edition:

	wget 'http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz&os=linux'

on Linux, or

	wget 'http://orientdb.com/download.php?email=unknown@unknown.com&file=orientdb-community-2.1.5.tar.gz&os=mac'

on MacOSX.

	tar xfz orientdb-community-2.1.5.tar.gz

Finally, download and extract dependencies, and build:

	wget 'http://user.informatik.uni-goettingen.de/~fyamagu/bjoern-radare/jars.tar.gz'
	tar xfz jars.tar.gz

	ant

## Usage

Begin by starting the bjoern-server:

	./bjoern-server.sh

Import some code

	./bjoern-import.sh /bin/ls

Ask for a gremlin-shell

	curl http://localhost:2480/shellcreate/

and connect to it:

	nc localhost 6000

You will find the database in `g`. Commands are terminated with the
sequence `\n\x00\n`. For example

	perl -e 'print "g.getVertices().count()\n\x00\n"' | nc localhost 6000

will return the number of vertices in the database.

Alternatively, you can explore the database using OrientDB studio by
pointing your browser to

	http://127.0.0.1:2480/

selecting the database `bjoernDB` and logging in with username `root`,
password `admin`.
