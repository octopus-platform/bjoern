#!/bin/sh
LD_LIBRARY_PATH=./lib/ java -Djava.library.path=lib -cp ./bin/bjoern.jar exporters.radare.RadareExporter $@
