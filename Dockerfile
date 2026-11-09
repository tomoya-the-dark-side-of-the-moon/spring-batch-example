FROM gradle:jdk21 AS builder

COPY ./src/ /tmp/src/
COPY ./gradle/ /tmp/gradle/
COPY ./gradlew /tmp/
COPY ./build.gradle /tmp/
COPY ./settings.gradle /tmp/
WORKDIR /tmp
RUN ./gradlew clean bootJar

FROM eclipse-temurin:21-jre-alpine AS runner
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    echo "Asia/Tokyo" > /etc/timezone && \
    apk del tzdata
WORKDIR /usr/app
COPY --from=builder /tmp/build/libs/spring-batch-example-0.0.1-SNAPSHOT.jar spring-batch-example.jar
