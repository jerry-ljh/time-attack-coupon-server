#!/bin/sh
SERVICE=${1:-API}
JOB_NAME=$2
SERVICE_NAME=coupon-api

echo $SERVICE_NAME

JAVA_OPTS="-Dspring.profiles.active=prod -Dsun.net.inetaddr.ttl=0"
java $JAVA_OPTS -jar ./coupon-api.jar
