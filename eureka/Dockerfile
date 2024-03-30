FROM amazoncorretto:17-alpine3.16-jdk

WORKDIR /app

COPY /build/libs/eureka-1.0.0.jar /app/app.jar

ENTRYPOINT java -jar app.jar
