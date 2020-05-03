#!/bin/bash

CRTDIR=$(pwd)
JAVA_OPT="-server -Xms256m -Xmx2048m -Duser.timezone=GMT+8"
JAVA_OPT="${JAVA_OPT} -XX:+PrintGCDetails -Xloggc:${CRTDIR}/logs/gc.log -XX:+PrintGCTimeStamps"
#JMX_OPT="-Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

PROJECT_HOME=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd | sed 's/\/sbin//')
LOG_DIR=${PROJECT_HOME}/logs
CLASSPATH="${PROJECT_HOME}/config:${PROJECT_HOME}/lib/*:xx.jar"

APPLICATION_MAIN=nny.build.data.CustomBuildDataApplication


args=""

for i in "$@"; do
    args="$args $i"
done

echo "JAVA_OPT: ${JAVA_OPT}"
java ${JAVA_OPT} ${JMX_OPT} -cp ${CLASSPATH} ${APPLICATION_MAIN} $args > /dev/null &
sleep 0.5
tail -100f ${CRTDIR}/logs/*log.log