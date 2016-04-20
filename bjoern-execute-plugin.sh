#!/bin/sh

cat $1 | curl -d @- http://localhost:2480/executeplugin/
