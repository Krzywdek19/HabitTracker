FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/eureka-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8071

CMD ["java","-jar","app.jar"]