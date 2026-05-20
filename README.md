# BDP (Big Data Platform)

모노레포: **레거시 WAR**와 **InsightBoard(신규 스택)** 가 분리되어 있습니다.

| 모듈 | 경로 | 설명 |
|------|------|------|
| Legacy | [legacy/](legacy/README.md) | Maven WAR — CBoard + 신한 커스텀 (단독 빌드·실행) |
| InsightBoard | [insight-board/](insight-board/README.md) | Spring Boot 3.4 + React 19 멀티모듈 백엔드 |

## InsightBoard (신규 스택)

리빌드된 BI 대시보드는 **insight-board/** 에 있습니다. 백엔드는 `common`, `api`, `web`, `external`, `app` Gradle 모듈로 구성됩니다.

| 문서 | 설명 |
|------|------|
| [docs/01-PROJECT_ASSESSMENT.md](docs/01-PROJECT_ASSESSMENT.md) | 심각성·문제점 평가 |
| [docs/02-REBUILD_ROADMAP.md](docs/02-REBUILD_ROADMAP.md) | 리빌드 로드맵 |
| [docs/03-ERD_PREDICTION.md](docs/03-ERD_PREDICTION.md) | 쿼리 기반 ERD |
| [docs/04-MIGRATION_PYTHON.md](docs/04-MIGRATION_PYTHON.md) | Python 이전 가이드 |
| [docs/05-MIGRATION_NODE.md](docs/05-MIGRATION_NODE.md) | Node.js 이전 가이드 |
| [docs/06-OPEN_DATA_AND_CRAWLING.md](docs/06-OPEN_DATA_AND_CRAWLING.md) | Open API·크롤링 |
| [docs/07-AWS_DEPLOYMENT.md](docs/07-AWS_DEPLOYMENT.md) | ECS/EC2 배포 |
| [docs/08-REBUILD_DIRECTION_V2.md](docs/08-REBUILD_DIRECTION_V2.md) | 모듈 분리·CBoard·Boot 방향 |
| [docs/09-CBOARD_SECURITY_AUDIT.md](docs/09-CBOARD_SECURITY_AUDIT.md) | CBoard 보안 검증 |
| [docs/10-CBOARD_LIBRARY_VS_REBUILD.md](docs/10-CBOARD_LIBRARY_VS_REBUILD.md) | CBoard JAR vs 자가 구현 |
| [docs/11-CBOARD_PARITY_ROADMAP.md](docs/11-CBOARD_PARITY_ROADMAP.md) | CBoard 기능 패리티 로드맵 |
| [insight-board/README.md](insight-board/README.md) | **InsightBoard** 실행 가이드 |

### 빠른 실행 (신규)

```bash
cd insight-board/backend
./gradlew :modules:app:bootRun
```

기본 계정: `admin` / `admin`

### 레거시 단독 실행

```bash
cd legacy
mvn clean package -DskipTests
mvn tomcat7:run
```
