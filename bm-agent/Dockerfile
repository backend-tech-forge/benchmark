FROM amazoncorretto:17-alpine3.16-jdk
ARG JAR_PATH=/build/libs

WORKDIR /app

COPY $JAR_PATH/bm-agent-1.0.0.jar /app/app.jar

ENTRYPOINT java -jar app.jar
