FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/PlaygroundMVC-0.0.1.jar PlaygroundMVC-0.0.1.jar

COPY config/redisson.yaml /app/config/redisson.yaml
COPY config/application.properties /app/config/application.properties

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar PlaygroundMVC-0.0.1.jar"]