#!/bin/sh

LD_LIBRARY_PATH=./lib/ ORIENTDB_HOME=orientdb-community-2.1.5/ java -jar ./bin/server.jar -Djava.library.path=lib
