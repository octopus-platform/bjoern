#!/bin/sh
LD_LIBRARY_PATH=./lib/ java -Djava.library.path=lib -jar ./bin/bjoern-radare.jar $@
