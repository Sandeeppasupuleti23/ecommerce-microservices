FROM eclipse-temurin:17-jre
WORKDIR /app
COPY eureka-server/target/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
