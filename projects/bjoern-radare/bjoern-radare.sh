#!/bin/sh

BASEDIR=$(dirname "$0")

java -cp "$BASEDIR/build/libs/bjoern-radare.jar:$BASEDIR/jars/*" bjoern.input.radare.RadareExporterMain $@
