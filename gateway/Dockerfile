FROM --platform=linux/arm64/v8 bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=/target/*-spring-boot.jar
COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Shanghai

EXPOSE 13580
ENTRYPOINT ["java","-jar","/app.jar"]