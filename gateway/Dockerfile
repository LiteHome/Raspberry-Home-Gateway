FROM --platform=linux/arm64/v8 eclipse-temurin:17.0.6_10-jre-jammy
ARG JAR_FILE=/target/*-spring-boot.jar
COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Shanghai

EXPOSE 13580
ENTRYPOINT ["java","-jar","/app.jar"]