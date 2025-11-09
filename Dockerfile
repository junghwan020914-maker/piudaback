# Build Stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon
RUN cp $(ls -1 build/libs/*.jar | head -n1) /app/app.jar

# Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app
VOLUME /config
ENV TZ=Asia/Seoul
COPY --from=build /app/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --spring.profiles.active=prod"]
