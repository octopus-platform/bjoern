#!/bin/sh
#
# Arguments:
# $1 - binary (passed to RadareExporter
# $2 - database name (optional, passed to BjoernImport)
#
# Note: All duplicate rows are removed before import.

TMP=$(mktemp -d)
trap "rm -rf $TMP" EXIT

./bjoern-radare.sh $1 -outdir $TMP || exit 1

cat $TMP/nodes.csv | sort -r | uniq > nodes.csv
cat $TMP/edges.csv | sort -r | uniq > edges.csv

if [ -z $2 ]
then
	./bjoern-csvimport.sh
else
	./bjoern-csvimport.sh -dbname $2
fi
