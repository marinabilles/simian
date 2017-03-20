#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
FAILURE="$1/failures.xml"
if [ -f "$FAILURE" ]
then
    incons=`cat "$FAILURE" | grep \<failure\> | wc -l`
else
    incons=0
fi
current=`pwd`
cd ${DIR}
mvn -q exec:java -Dexec.mainClass="de.crispda.sola.multitester.util.ExecutionLogParser" -Dexec.args="\"$current/$1.xml\""
printf "Number of inconsistencies: %d\n" "$incons"

