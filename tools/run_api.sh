IMAGE=$API_IMAGE
docker run --name=coupon-api -p 80:8080 -t $IMAGE