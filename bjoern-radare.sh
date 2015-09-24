#!/bin/sh
LD_LIBRARY_PATH=./lib/ java -Djava.library.path=lib -jar ./bin/bjoern-radare.jar $@
java -jar ./bin/orientdbImporter.jar nodes.csv edges.csv
