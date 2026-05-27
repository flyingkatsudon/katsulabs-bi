# Katsulabs BI

**Katsulabs BI** — BI dashboard (Spring Boot 4 + React). Gradle 프로젝트명: `katsulabs-bi`.

## Quick start

```bash
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-h2'
cd frontend && npm install && npm run dev
```

Open http://localhost:5173 — login **admin01** / **admin123**.

## Docs

- [AGENTS.md](AGENTS.md) — agent / dev reference
- [docs/harness/README.md](docs/harness/README.md) — main/sub-agent harness
- [docs/migration/README.md](docs/migration/README.md) — architecture & migration notes
- [frontend/README.md](frontend/README.md) — UI development

## Repository folder name

Git remote·로컬 폴더를 `katsulabs-bi`로 맞추려면 워크스페이스 디렉터리를 직접 rename 하면 됩니다 (Git 히스토리와 무관).
