FROM eclipse-temurin:17-jre
WORKDIR /app
COPY inventory-service/target/inventory-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]