FROM eclipse-temurin:17-jre
WORKDIR /app
COPY config-server/target/config-server-*.jar app.jar
COPY config-repo /config-repo
ENV CONFIG_REPO_LOCATION=file:/config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
