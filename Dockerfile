# 1단계: 빌드 스테이지 (Gradle 빌드)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .

# 윈도우 환경에서 작성 시 권한 에러 방지 및 빌드 진행
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 스테이지 (실제 서버 구동)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일을 실행 스테이지로 복사
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]