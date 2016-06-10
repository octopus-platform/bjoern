#!/bin/sh

OPTS_FROM_ORIENTDB="-server -Xmx512m -Djna.nosys=true -XX:+HeapDumpOnOutOfMemoryError -Djava.awt.headless=true -Dfile.encoding=UTF8 -Drhino.opt.level=9 -Dprofiler.enabled=true"

HOME=`dirname $0`

cd $HOME

exec java $JAVA_OPTS $OPTS_FROM_ORIENTDB -cp "jars/*" octopus.OctopusMain
