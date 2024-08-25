FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8099
ENTRYPOINT ["java","-jar","/app.jar"]
