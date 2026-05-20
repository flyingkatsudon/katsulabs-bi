# BDP Next — 리빌드 스캐폴드

레거시 `bdp` WAR를 대체하기 위한 **Spring Boot 3 + Gradle + H2 + JWT + JPA** 프로젝트입니다.

## 요구 사항

- Java 21+
- (선택) Node 22+ — 프론트엔드

## 빠른 실행

```bash
cd bdp-next/backend
./gradlew bootRun
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- H2 Console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:bdp`)

### 기본 계정

| 필드 | 값 |
|------|-----|
| login | `admin` |
| password | `admin` |

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}'
```

## 프론트엔드 (React)

```bash
cd bdp-next/frontend
npm install
npm run dev
```

Vite proxy → `http://localhost:8080`

## 테스트

```bash
cd bdp-next/backend
./gradlew test jacocoTestReport
```

## 문서

- [프로젝트 평가](../docs/01-PROJECT_ASSESSMENT.md)
- [리빌드 로드맵](../docs/02-REBUILD_ROADMAP.md)
