FROM openjdk:17-slim
COPY target/*.jar /app/app.jar
COPY src/main/resources/application-prod.yaml /app/application.yaml
WORKDIR /app
CMD ["java", "-jar", "/app/app.jar", "-Duser.timezone=Asia/Shanghai", "--spring.config.location=/app/application.yaml"]
EXPOSE 8080
