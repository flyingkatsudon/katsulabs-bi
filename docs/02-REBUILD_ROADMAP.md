# BDP 리빌드 로드맵

요구사항 1~9를 단계별로 실행하기 위한 계획입니다. 구현 코드는 [`bdp-next/`](../bdp-next/)에 둡니다.

---

## Phase 0 — 기반 (현재 진행)

| # | 요구사항 | 산출물 | 상태 |
|---|----------|--------|------|
| 0 | 현황 평가 | `docs/01-PROJECT_ASSESSMENT.md` | ✅ |
| 0 | ERD 예측 | `docs/03-ERD_PREDICTION.md` | ✅ |
| 0 | 마이그레이션 가이드 | `docs/04`, `05` | ✅ |
| 0 | Open API·크롤링 | `docs/06` | ✅ |
| 0 | AWS 배포 | `docs/07` | ✅ |
| 1 | Gradle + 최신 Java/Spring | `bdp-next/backend` | 🚧 스캐폴드 |
| 2 | H2 + Flyway clone-run | `V1__*.sql`, `application-local.yml` | 🚧 |
| 4 | JWT | `SecurityConfig`, `/api/v1/auth/*` | 🚧 |
| 5 | JPA | `dashboard_*` 엔티티 | 🚧 |
| 6 | React | `bdp-next/frontend` | 🚧 |
| 3 | 테스트 | `@WebMvcTest`, `@DataJpaTest` | 🚧 초기 |
| 7 | Python/Node 가이드 | 문서 | ✅ |
| 8 | 데이터 파이프라인 | 문서 + `ingestion` 패키지 스텁 | 📋 |
| 9 | ECS/EC2 | `docs/07`, Dockerfile | 🚧 |

---

## Phase 1 — 백엔드 MVP (실행 가능)

1. **인증**: JWT 발급/검증, BCrypt, `/api/v1/auth/login`, `/api/v1/auth/refresh`
2. **CBoard 메타 API**: datasource, widget, board, dataset CRUD (JPA)
3. **인사이트 API 스텁**: `/api/v1/report/trends` — H2 시드 데이터 조회
4. **OpenAPI**: springdoc-openapi `/swagger-ui.html`
5. **테스트**: AuthController, BoardController, Repository 전부

**완료 기준:** `./gradlew bootRun` 후 H2 콘솔·Swagger·로그인 동작.

---

## Phase 2 — 프론트 + 계약 고정

1. React 19 + Vite + TypeScript + React Query
2. OpenAPI codegen (`openapi-typescript`)
3. 로그인 → 대시보드 목록 → 위젯 미리보기
4. CORS + JWT Bearer

---

## Phase 3 — 데이터·레거시 이전

1. PostgreSQL 프로덕션 프로필 (`spring.profiles.active=prod`)
2. MyBatis XML → Spring Data / Native Query 단계적 포팅
3. 배치 ingestion (Open API → S3 → DB)
4. CBoard Angular 화면 기능별 React 이전

---

## Phase 4 — 클라우드

1. Docker multi-stage build
2. ECS Fargate + ALB + RDS
3. Secrets Manager (JWT secret, DB)
4. GitHub Actions CI/CD

---

## 기술 스택 (목표)

| 레이어 | 선택 |
|--------|------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.4.x |
| Build | Gradle 8.14 (Wrapper) |
| ORM | Spring Data JPA + Flyway |
| Security | Spring Security 6 + JWT (jjwt 0.12) |
| API Docs | springdoc-openapi 2.x |
| DB (local) | H2 2.2 |
| DB (prod) | PostgreSQL 16 / Aurora |
| Front | React 19, Vite 6, TypeScript 5 |
| Cache | Redis (선택, Phase 3) |

---

## 디렉터리 구조

```
bdp-next/
├── backend/          # Spring Boot
├── frontend/         # React SPA
├── docker-compose.yml
└── README.md         # clone & run
```

---

## 마이그레이션 원칙 (Python/Node 대비)

- **OpenAPI First**: 모든 REST는 `openapi.yaml` 또는 springdoc 생성 스펙이 단일 진실 공급원
- **도메인 모델 분리**: `domain/` 패키지는 프레임워크 무관 POJO → 다른 언어로 1:1 매핑
- **포트·어댑터**: `application` / `infrastructure` 레이어 분리
