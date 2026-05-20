# InsightBoard — Spring Boot + React (CBoard UI)

CBoard-compatible BI dashboard rebuilt on Spring Boot 3.4 + React 19 (no external CBoard JAR).

## Quick start

```bash
cd insight-board/backend
./gradlew bootRun
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

## Stack

- **Backend:** Java 21, Spring Boot 3.4.5, JPA, H2 (local), JWT
- **Frontend:** React 19, Vite, AdminLTE-style assets under `public/cboard/`
- **API:** `/cboard/*` compatibility layer + `/api/v1/*` REST

## Tests & build

```bash
cd insight-board/backend && ./gradlew test
cd insight-board/frontend && npm run build
```

## Docker

```bash
cd insight-board
docker compose up --build
```
