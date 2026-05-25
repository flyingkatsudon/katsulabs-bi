# 기술 스택 현대화 (Migration)

Gradle / Spring Boot 4 / React 스택으로 전환 완료. **레거시 Maven WAR** (`pom.xml`, `src/main/`, Tomcat) 는 저장소에서 제거되었습니다.

## 문서 목록

| 문서 | 내용 |
|------|------|
| [00-현재-기술-스택.md](./00-현재-기술-스택.md) | 스택 정리 (히스토리 포함) |
| [01-취약점-및-기술-부채.md](./01-취약점-및-기술-부채.md) | CVE·구조 부채 |
| [02-마이그레이션-로드맵.md](./02-마이그레이션-로드맵.md) | Phase별 계획 |
| [03-목표-아키텍처.md](./03-목표-아키텍처.md) | Clean Architecture |
| [MYBATIS-XML.md](./MYBATIS-XML.md) | 필수 mapper XML |
| [Polyglot 가이드](./polyglot/README.md) | Kotlin / Node / Python |

## API · 실행

| 항목 | 상태 |
|------|------|
| Flyway V1–V7 (H2·Postgres) | ✅ |
| `POST /api/v1/auth/login` | ✅ |
| CRUD boards / datasets / datasources / widgets / categories | ✅ |
| `POST /api/v1/aggregate` | ✅ |
| React Configuration + Dashboard | ✅ |
| 통합 테스트 | ✅ |

## 빠른 시작

```bash
./gradlew test
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-h2'
cd frontend && npm install && npm run dev
```

- UI: http://localhost:5173  
- API: http://localhost:8081/api/v1/health  
- Login: **admin01** / **admin123**

## Postgres

```bash
docker compose -f docker-compose.postgres.yml up -d
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-postgres'
```

단일 테넌트 (legacy `business_code` 제거됨, Flyway V10)
