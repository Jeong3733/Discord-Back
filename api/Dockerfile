FROM openjdk:17-buster AS builder

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*

# 프로젝트 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 실행 권한 설정 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew -x test clean bootJar

# 최종 이미지 생성
FROM openjdk:17-buster

# 환경 변수 배포 환경으로 설정
ENV SPRING_PROFILES_ACTIVE prod

COPY --from=builder build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/app.jar"]