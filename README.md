# BDP (Big Data Platform) — Legacy

신한 커스텀 + [CBoard](https://github.com/yzhang1984/CBoard) 기반 레거시 WAR 프로젝트입니다.

## 리빌드 진행 중

현재 코드베이스의 **심각도·기술 부채 분석** 및 **신규 스택**은 아래를 참고하세요.

| 문서 | 설명 |
|------|------|
| [docs/01-PROJECT_ASSESSMENT.md](docs/01-PROJECT_ASSESSMENT.md) | 심각성·문제점 평가 |
| [docs/02-REBUILD_ROADMAP.md](docs/02-REBUILD_ROADMAP.md) | 리빌드 로드맵 |
| [docs/03-ERD_PREDICTION.md](docs/03-ERD_PREDICTION.md) | 쿼리 기반 ERD |
| [docs/04-MIGRATION_PYTHON.md](docs/04-MIGRATION_PYTHON.md) | Python 이전 가이드 |
| [docs/05-MIGRATION_NODE.md](docs/05-MIGRATION_NODE.md) | Node.js 이전 가이드 |
| [docs/06-OPEN_DATA_AND_CRAWLING.md](docs/06-OPEN_DATA_AND_CRAWLING.md) | Open API·크롤링 |
| [docs/07-AWS_DEPLOYMENT.md](docs/07-AWS_DEPLOYMENT.md) | ECS/EC2 배포 |
| [docs/08-REBUILD_DIRECTION_V2.md](docs/08-REBUILD_DIRECTION_V2.md) | **모듈 분리·CBoard·Boot 4 방향** |
| [docs/09-CBOARD_SECURITY_AUDIT.md](docs/09-CBOARD_SECURITY_AUDIT.md) | CBoard 보안 검증 |
| [bdp-next/README.md](bdp-next/README.md) | **신규** Spring Boot + React (`clone & run`) |

### 신규 스택 빠른 실행

```bash
cd bdp-next/backend
./gradlew bootRun
```

기본 계정: `admin` / `admin`
