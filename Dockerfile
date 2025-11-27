# 1단계: 빌드용 (Gradle + JDK)
FROM gradle:8.7-jdk17 AS builder
WORKDIR /workspace

# Gradle 캐시를 좀 더 잘 쓰고 싶으면 settings.gradle, build.gradle, gradle 폴더 먼저 COPY하고
# 그다음에 소스 COPY해도 됨. 일단은 심플하게 전체 복사.
COPY . .

# 여기서 항상 깨끗하게 빌드
RUN ./gradlew clean bootJar --no-daemon

# 2단계: 실행용 (JRE만 있는 가벼운 이미지)
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌더 단계에서 만들어진 JAR 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# (선택) JVM 옵션
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
