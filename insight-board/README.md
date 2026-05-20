# InsightBoard — Spring Boot + React (CBoard UI)

CBoard-compatible BI dashboard rebuilt on Spring Boot 3.4 + React 19 (no external CBoard JAR).

## Quick start

```bash
cd insight-board/backend
./gradlew :modules:app:bootRun
```

In another terminal:

```bash
cd insight-board/frontend
npm install
npm run dev
```

| Item | Value |
|------|--------|
| API | http://localhost:8080 |
| Web | http://localhost:5173 |
| Login | `/login` |
| Default user | `admin` / `admin` |

## Backend 멀티모듈 (Clean Architecture)

```
backend/modules/
├── common/     # 공유 예외·유틸
├── api/        # CBoard/dashboard — domain · application · infrastructure · presentation
├── web/        # 신한 인사이트·리포트 — domain · application · infrastructure · presentation
├── external/   # JDBC DataProvider 등 외부 연동 (api.domain.port 구현)
└── app/        # Spring Boot 진입점, Security(JWT), 설정, DB 마이그레이션
```

의존 방향: `app` → `api`, `web`, `external` → `common` · `external` → `api` (포트 구현)

## Stack

- **Backend:** Java 21, Spring Boot 3.4.5, Gradle 멀티모듈, JPA, H2 (local), JWT
- **Frontend:** React 19, Vite, AdminLTE-style assets under `public/cboard/`
- **API:** `/cboard/*` compatibility layer + `/api/v1/*` REST

## Tests & build

```bash
cd insight-board/backend && ./gradlew test
cd insight-board/frontend && npm run build
```

JAR: `modules/app/build/libs/insight-board.jar`

## Docker

```bash
cd insight-board
docker compose up --build
```

## 레거시

레거시 WAR는 저장소 루트의 [legacy/](../legacy/README.md) 에서 단독 빌드·실행합니다.
