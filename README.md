# Insight Board

**Katsulabs Insight Board** — BI dashboard (Spring Boot 4 + React).

## Quick start

```bash
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-h2'
cd frontend && npm install && npm run dev
```

Open http://localhost:5173 — login **admin01** / **admin123**.

## Docs

- [AGENTS.md](AGENTS.md) — agent / dev reference
- [docs/migration/README.md](docs/migration/README.md) — architecture & migration notes
- [frontend/README.md](frontend/README.md) — UI development
