FROM amazoncorretto:17-alpine3.16-jdk
ARG JAR_PATH=/bm-agent/build/libs

RUN echo "JAR_PATH: ${JAR_PATH}"
RUN echo "JAR_PATH: $JAR_PATH"
RUN echo "${JAR_PATH}/bm-agent-1.0.0.jar"
RUN echo "$JAR_PATH/bm-agent-1.0.0.jar"

WORKDIR /app



COPY ${JAR_PATH}/bm-agent-1.0.0.jar /app/app.jar

ENTRYPOINT java -jar app.jar
