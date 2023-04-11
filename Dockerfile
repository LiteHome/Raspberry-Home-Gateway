FROM eclipse-temurin:17.0.6_10-jre-jammy
ARG JAR_FILE=/root/javaProject/Raspberry-Home-Gateway/gateway/target/gateway-0.0.1-SNAPSHOT-spring-boot.jar
COPY ${JAR_FILE} app.jar

RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 13580
ENTRYPOINT ["java","-jar","/app.jar"]