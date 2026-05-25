# 다언어 마이그레이션 가이드 (Polyglot)

본 프로젝트는 **Java 21 + Spring Boot 4** 를 메인 런타임으로 두되, 팀·서비스 상황에 따라 **Kotlin**, **Node.js**, **Python** 으로 일부를 분리할 수 있도록 API·계약을 염두에 두고 설계합니다.

## 공통 원칙

| 원칙 | 설명 |
|------|------|
| **계약 우선** | OpenAPI(JSON)·DB 스키마(Flyway)를 단일 진실 공급원으로 |
| **Jackson JSON** | 필드명·날짜(ISO-8601) 형식 통일 — 각 언어에서 동등 라이브러리 사용 |
| **세션/인증** | 현재는 서버 세션; 이후 JWT·OAuth2 로 확장 가능 |
| **Strangler** | 레거시 WAR와 신규 API 병행 후 점진 교체 |

## API 베이스 URL

| 환경 | URL |
|------|-----|
| Boot 4 로컬 | `http://localhost:8081/api/v1` |
| 레거시 | `http://localhost:8080/bdp/cboard/dashboard` (점진 폐기) |

## 언어별 문서

- [Kotlin](./kotlin.md) — JVM 공존, Gradle 멀티모듈
- [Node.js](./node.md) — React/Vite BFF·마이크로서비스
- [Python](./python.md) — 데이터 파이프라인·ML 보조 서비스

## 권장 분리 경계

```
[React/Vite] ──HTTP──► [Boot 4 API] ──JDBC──► [Postgres/H2]
                            │
              (선택) gRPC/HTTP
                            ▼
              [Python ETL]  [Node BFF]  [Kotlin 배치]
```

- **Boot 4**: 인증, RBAC, Board/Dataset 메타, DataProvider 오케스트레이션
- **Python**: 대용량 ETL, 모델 추론 (CBoard 코어 밖)
- **Node**: 프론트 전용 BFF, SSR, 실시간 알림
- **Kotlin**: JVM 내부 배치·Kylin 연동 (Java와 모듈 공유)

## JSON 스키마 예 (Board 목록)

```json
[
  {
    "id": 1,
    "name": "Demo Board",
    "userId": "admin01",
    "userName": "Admin",
    "categoryId": 1,
    "categoryName": "Demo",
    "layoutJson": null,
    "createdAt": "2026-05-25T00:00:00Z",
    "updatedAt": "2026-05-25T00:00:00Z"
  }
]
```

각 언어 가이드에서 동일 페이로드로 직렬화/역직렬화하는 방법을 설명합니다.
