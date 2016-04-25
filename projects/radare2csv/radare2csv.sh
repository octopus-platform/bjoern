#!/bin/sh

BASEDIR=$(dirname "$0")

java -cp "$BASEDIR/build/libs/radare2csv.jar:$BASEDIR/jars/*" bjoern.input.radare.RadareExporterMain $@
