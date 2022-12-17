IMAGE=$BATCH_IMAGE
JOB_NAME="couponIssueJob"
VERSION=$(date +%s)
ARGS="threadCount=1 couponTitle=TIME_SALE version=$VERSION"
echo "java -server -Duser.timezone=UTC -Dspring.batch.job.names=$JOB_NAME -Dspring.profiles.active=prod -jar /app/coupon-batch.jar $ARGS"
docker run $IMAGE --entrypoint "java -server -Duser.timezone=UTC -Dspring.batch.job.names=$JOB_NAME -Dspring.profiles.active=prod -jar /app/coupon-batch.jar $ARGS"