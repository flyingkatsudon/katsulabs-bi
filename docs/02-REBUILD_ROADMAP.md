# BDP 리빌드 로드맵

요구사항 1~9를 단계별로 실행하기 위한 계획입니다. 구현 코드는 [`insight-board/`](../insight-board/)에 둡니다.

> **v2 방향 (모듈 분리·CBoard 보안·Boot 4):** [08-REBUILD_DIRECTION_V2.md](./08-REBUILD_DIRECTION_V2.md) · [09-CBOARD_SECURITY_AUDIT.md](./09-CBOARD_SECURITY_AUDIT.md)

---

## 모듈 구조 (v2)

| 모듈 | 레거시 | 역할 |
|------|--------|------|
| `dashboard` | `org.cboard.*` | CBoard BI 메타·DataProvider |
| `web` | `com.shinhan.*` | 인사이트·리포트·GA |
| `common` | — | 공유 커널 |
| `app` | WAR 진입점 | Boot 조립·JWT·OpenAPI |

---

## Phase 0 — 기반 (현재 진행)

| # | 요구사항 | 산출물 | 상태 |
|---|----------|--------|------|
| 0 | 현황 평가 | `docs/01-PROJECT_ASSESSMENT.md` | ✅ |
| 0 | ERD 예측 | `docs/03-ERD_PREDICTION.md` | ✅ |
| 0 | 마이그레이션 가이드 | `docs/04`, `05` | ✅ |
| 0 | Open API·크롤링 | `docs/06` | ✅ |
| 0 | AWS 배포 | `docs/07` | ✅ |
| 0b | 모듈 분리 설계 | `08-REBUILD_DIRECTION_V2.md` | ✅ |
| 0c | CBoard 보안 체크리스트 | `09-CBOARD_SECURITY_AUDIT.md` | ✅ |
| 1 | Gradle + 최신 Java/Spring | `insight-board/backend` → **4모듈** | 🚧 스캐폴드 |
| 2 | H2 + Flyway clone-run | `V1__*.sql`, `application-local.yml` | 🚧 |
| 4 | JWT | `SecurityConfig`, `/api/v1/auth/*` | 🚧 |
| 5 | JPA | `dashboard_*` 엔티티 | 🚧 |
| 6 | React | `insight-board/frontend` | 🚧 |
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

## 기술 스택 (목표, v2)

| 레이어 | 선택 |
|--------|------|
| Runtime | Java 21 |
| Framework | Spring Boot **3.4.x → 4.0.x** (POC 후, [08](./08-REBUILD_DIRECTION_V2.md)#4) |
| Modules | `common`, `dashboard`, `web`, `app` |
| Build | Gradle 8.14 + **OWASP/SBOM** |
| ORM | Spring Data JPA; `web`은 복잡 SQL 시 JDBC/MyBatis |
| Security | JWT; **fastjson·druid 금지** |
| CBoard | 업스트림 JAR ❌ → **dashboard 재구현** |
| DB (local) | H2 (메타+분석 시드) |
| DB (prod) | MySQL/Aurora (dashboard) + PostgreSQL (web) |
| Front | React 19 (AngularJS 미이전) |

---

## 디렉터리 구조 (v2 목표)

```
insight-board/
├── modules/
│   ├── common/
│   ├── dashboard/    # 구 CBoard
│   ├── web/          # 구 shinhan
│   └── app/
├── frontend/
└── README.md
```

현재는 단일 `backend/` 스캐폴드 → Phase 0에서 멀티모듈로 분리 예정.

---

## 마이그레이션 원칙 (Python/Node 대비)

- **OpenAPI First**: 모든 REST는 `openapi.yaml` 또는 springdoc 생성 스펙이 단일 진실 공급원
- **도메인 모델 분리**: `domain/` 패키지는 프레임워크 무관 POJO → 다른 언어로 1:1 매핑
- **포트·어댑터**: `application` / `infrastructure` 레이어 분리
