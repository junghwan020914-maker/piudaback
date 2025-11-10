# Dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 산출물 JAR 경로는 프로젝트에 맞게 수정 (예: build/libs/*.jar)
ARG JAR=build/libs/*.jar
COPY ${JAR} app.jar

# (선택) JVM 옵션
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
