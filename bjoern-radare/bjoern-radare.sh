#!/bin/sh
java -cp "./build/libs/bjoern-radare.jar:jars/*" bjoern.input.radare.RadareExporterMain $@
