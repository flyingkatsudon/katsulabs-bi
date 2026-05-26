# AGENTS.md

## Project overview

Katsulabs BI (레거시 BI 포크) — **Java 21 / Gradle / Spring Boot 4** API (`modules/`), **React + Vite** UI (`frontend/`). Package: `com.katsulabs.bi`, main class: `KatsulabsBiApplication`.

Legacy Maven WAR (`pom.xml`, `src/main/`, Tomcat) has been **removed**. UI styles load from `frontend/public/katsulabs-bi/` (AdminLTE assets, URL `/katsulabs-bi/`).

## Prerequisites

- **JDK 21+**
- **Node.js** (frontend)
- Optional: **Docker** for Postgres profile

## Run (local H2 demo)

```bash
chmod +x scripts/run-api.sh scripts/stop-dev.sh
./scripts/run-api.sh
cd frontend && npm install && npm run dev
```

`./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-h2'` also works, but **Ctrl+C 후 Gradle이 `EXECUTING`에서 멈추거나 포트가 남는 경우**가 있습니다. 그때는 `./scripts/stop-dev.sh` 로 8081·5173 등을 정리하세요. **권장:** `./scripts/run-api.sh` (JVM 직접 실행 → 종료가 확실함).

- API: `http://localhost:8081/api/v1/health`
- UI: `http://localhost:5173` (proxies `/api` → Boot)
- Login (password **admin123** for all demo users):
  - **admin01** — Super Admin (users, datasources, boards)
  - **manager01** — Manager (boards/widgets/categories; read-only datasources)
  - **viewer01** — Viewer (dashboard view + aggregate only)

Flyway seeds **Chart Gallery**, **Demo Board**, per-datasource sample dashboards under category **Data Source Samples** (`FoodMart Sample Dashboard`, `Real Estate Sample Dashboard`, `Economic Indicators Sample Dashboard`), demo datasources (`demo_source`, `demo_realestate`, `demo_economic`) and datasets (`foodmart_sample`, `realestate_korea`, `economic_indicators`).

User admin API: `GET/POST/PUT/DELETE /api/v1/users` (Super Admin only). UI: **Configuration → Users**.

## Postgres profile

```bash
docker compose -f docker-compose.postgres.yml up -d
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-postgres'
```

## Stop local servers

```bash
./scripts/stop-dev.sh          # API(8081…) + Vite(5173) 프로세스 종료
./scripts/stop-dev.sh --gradle # 위 + Gradle daemon 중지
```

로컬 H2/Postgres 프로필은 `server.shutdown=immediate` 로 dev 종료 대기를 줄였습니다.

## Build & test

```bash
./gradlew test
./gradlew :modules:api:bootJar
cd frontend && npm run build
```

## Single JAR (UI embedded)

```bash
./scripts/run-prod-jar.sh
# or: cd frontend && npm ci && npm run build && ./gradlew :modules:api:bootJar
#     java -jar modules/api/build/libs/katsulabs-bi.jar --spring.profiles.active=local-h2,prod
```

Open `http://localhost:8081/`. Details: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md).

## MyBatis

Required mapper XML: `modules/infrastructure/src/main/resources/mapper/*.xml` — see [docs/migration/MYBATIS-XML.md](docs/migration/MYBATIS-XML.md).

## Migration docs

[docs/migration/README.md](docs/migration/README.md)

## Supply chain (dependencies)

- **Backend**: Gradle resolves **Maven Central only** (`build.gradle.kts`). No Aliyun HTTP mirrors, Kylin/Druid/Fastjson, or legacy `pom.xml`.
- **Frontend**: npm packages from the public registry (`package-lock.json`). No runtime CDN scripts; static AdminLTE assets are vendored under `frontend/public/katsulabs-bi/`.
- **Removed**: China map / Baidu chart types (`chinaMap`, `chinaMapBmap`), legacy Tomcat 레거시 BI iframe editor (`VITE_LEGACY_*`).
- **Optional**: `./gradlew dependencyCheckAnalyze` when OWASP dependency-check plugin is added (see migration docs).
