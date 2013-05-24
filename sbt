#!/bin/sh


JAVA_OPTS="
    -Xmx2048M
    -XX:MaxPermSize=350m
    -XX:+CMSClassUnloadingEnabled
"


java -jar "$(dirname "$0")"/sbt-launch-0.12.3.jar "$@"
