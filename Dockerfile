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
RUN addgroup springgroup && adduser -S springuser -G springgroup
USER springuser
WORKDIR /usr/app
RUN chown -R springuser:springgroup /usr/app
COPY --from=builder --chown=springuser:springgroup /tmp/build/libs/spring-batch-example-0.0.1-SNAPSHOT.jar spring-batch-example.jar
CMD ["java", "-jar", "spring-batch-example.jar"]
