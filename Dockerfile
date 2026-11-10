#FROM gradle:jdk21 AS builder
FROM ghcr.io/graalvm/graalvm-community:25 AS builder
COPY ./src/ /tmp/src/
COPY ./gradle/ /tmp/gradle/
COPY ./gradlew /tmp/
COPY ./build.gradle /tmp/
COPY ./settings.gradle /tmp/
WORKDIR /tmp
RUN ./gradlew clean nativeCompile

FROM oraclelinux:9-slim AS runner
ENV TZ=Asia/Tokyo
RUN microdnf install -y shadow-utils && \
    groupadd springgroup && useradd -r -g springgroup springuser && \
    microdnf remove -y shadow-utils && microdnf clean all
WORKDIR /usr/app
COPY --from=builder --chown=springuser:springgroup /tmp/build/native/nativeCompile/spring-batch-example spring-batch-example
USER springuser
CMD ["/usr/app/spring-batch-example"]

