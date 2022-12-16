FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine-slim as builder

ENV WORK /workspace
ENV COUPON_CORE_HOME /coupon-core
ENV COUPON_API_HOME /coupon-api
ENV COUPON_BATCH_HOME /coupon-batch

RUN mkdir -p $WORK
RUN mkdir -p $WORK/gradle
RUN mkdir -p $WORK/$COUPON_CORE_HOME
RUN mkdir -p $WORK/$COUPON_API_HOME
RUN mkdir -p $WORK/$COUPON_BATCH_HOME

COPY build.gradle.kts settings.gradle.kts gradlew $WORK/
COPY gradle/ $WORK/gradle
COPY ./tools/entrypoint_*.sh $WORK/
COPY .$COUPON_CORE_HOME/build.gradle.kts $WORK/$COUPON_CORE_HOME
COPY .$COUPON_CORE_HOME/src/ $WORK/$COUPON_CORE_HOME/src
COPY .$COUPON_API_HOME/build.gradle.kts $WORK/$COUPON_API_HOME
COPY .$COUPON_API_HOME/src/ $WORK/$COUPON_API_HOME/src
COPY .$COUPON_BATCH_HOME/build.gradle.kts $WORK/$COUPON_BATCH_HOME
COPY .$COUPON_BATCH_HOME/src/ $WORK/$COUPON_BATCH_HOME/src

WORKDIR $WORK
RUN dos2unix gradlew
RUN ./gradlew clean build -x test --parallel --no-daemon

FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine-slim

ENV TZ "Asia/Seoul"
ENV APPLICATION_USER coupon


RUN adduser -D -g '' $APPLICATION_USER
RUN apk add bash

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

ARG STAGE
ARG SERVICE_NAME
ARG TAG
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

RUN echo "$STAGE / $SERVICE_NAME"

ENV SERVICE_NAME $SERVICE_NAME
ENV SPRING_PROFILES_ACTIVE $STAGE
ENV AWS_ACCESS_KEY_ID $AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY $AWS_SECRET_ACCESS_KEY

COPY --from=builder /workspace/coupon-api/build/libs/coupon-api.jar /app/coupon-api.jar
COPY --from=builder /workspace/coupon-batch/build/libs/coupon-batch.jar /app/coupon-batch.jar
COPY --from=builder /workspace/entrypoint_${SERVICE_NAME}_${STAGE}.sh /app/entrypoint.sh

WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["sh", "entrypoint.sh"]
