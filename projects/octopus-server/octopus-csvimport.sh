#!/bin/sh

BASEDIR=$(dirname "$0")

java -cp "$BASEDIR/jars/*" octopus.clients.importer.Import $@
