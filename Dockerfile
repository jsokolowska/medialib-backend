FROM openjdk:11-jdk-oraclelinux8
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dserver.port=8090","-jar","/app.jar"]