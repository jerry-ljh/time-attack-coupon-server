#!/bin/sh
SERVICE=${1:-API}
JOB_NAME=$2
SERVICE_NAME=coupon-batch

echo $SERVICE_NAME

JAVA_OPTS="-Dspring.profiles.active=prod -Dspring.batch.job.names=$JOB_NAME"
java $JAVA_OPTS -jar ./coupon-batch.jar
