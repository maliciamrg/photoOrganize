FROM openjdk:17-jdk-alpine
EXPOSE 8080/tcp
COPY target/photoOrganize-infrastructure-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
