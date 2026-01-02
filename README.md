### TOMO Backend

Spring Boot 기반의 TOMO 백엔드 서버입니다.
유저 인증(Firebase), 모임 관리, 친구 관리 등 주요 도메인 기능을 제공합니다.

본 저장소는 Java 21 / Spring Boot 3.5.5 기반으로 개발되었으며,
운영 환경에서는 Oracle DB + Nginx Reverse Proxy 구조로 배포됩니다.

📌 Tech Stack
Backend

Java 21

Spring Boot 3.5.5

Spring Web

Spring Data JPA

Spring Security

Spring Validation

Lombok

JJWT (0.11.5)

firebase-admin SDK

Database

Oracle DB

Build & Dependency

Gradle (Groovy DSL)

Dev Tools

Spring DevTools

JUnit 5

API 문서

springdoc-openapi-starter-webmvc-ui (Swagger UI)