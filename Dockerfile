FROM openjdk:17-jdk-alpine
ARG JAR_FILE=/target/*-spring-boot.jar
COPY ${JAR_FILE} app.jar

RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 13580
ENTRYPOINT ["java","-jar","/app.jar"]