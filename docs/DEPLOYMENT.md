# 단일 JAR 배포 (API + React UI)

로컬 개발은 API(`8081`) + Vite(`5173`) 분리 실행을 권장합니다.  
운영·데모용으로 **하나의 Spring Boot JAR**에 프론트 빌드를 넣어 서빙할 수 있습니다.

## 빌드

```bash
cd frontend && npm ci && npm run build
./gradlew :modules:api:bootJar
```

`frontend/dist`가 있으면 `copyFrontendDist`가 `classpath:/static/`에 복사합니다.  
산출물: `modules/api/build/libs/insight-board.jar`

## 실행 (H2 데모 + SPA)

```bash
java -jar modules/api/build/libs/insight-board.jar --spring.profiles.active=local-h2,prod
```

또는:

```bash
./scripts/run-prod-jar.sh
```

- UI·API: `http://localhost:8081/` (포트는 `application.yml`의 `server.port`)
- 헬스: `http://localhost:8081/api/v1/health`
- 로그인: **admin01** / **admin123** (manager01, viewer01 동일 비밀번호)

## 프로필

| 프로필 | 용도 |
|--------|------|
| `local-h2` | 파일 없는 임베디드 H2 + Flyway 시드 |
| `local-postgres` | Docker Postgres (`docker-compose.postgres.yml`) |
| `prod` | `insightboard.spa.enabled=true` — React 정적 파일 + SPA 폴백 |

DB 프로필과 `prod`를 함께 켭니다. 예: `local-h2,prod`, `local-postgres,prod`.

## 동작 요약

- `prod`에서 `SpaWebConfig`가 `classpath:/static/`을 서빙하고, 없는 경로는 `index.html`로 폴백합니다.
- Security: `GET` 비-API 경로는 허용, `/api/**`(헬스·로그인 제외)는 세션 인증 유지합니다.
- Vite dev proxy 없이도 브라우저가 같은 오리진에서 `/api/v1/*`를 호출합니다.

## Postgres 운영 예

```bash
docker compose -f docker-compose.postgres.yml up -d
cd frontend && npm ci && npm run build
./gradlew :modules:api:bootJar
java -jar modules/api/build/libs/insight-board.jar \
  --spring.profiles.active=local-postgres,prod
```

`application-local-postgres.yml`의 JDBC URL·계정을 환경에 맞게 조정하세요.

## CI

`ci.yml`의 `package` job이 프론트 빌드 후 `bootJar`를 만들고 JAR 안에 `static/index.html`이 있는지 검증합니다.
