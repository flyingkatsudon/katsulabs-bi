# InsightBoard Backend

Gradle 멀티모듈 + Clean Architecture 레이어.

## 모듈

| 모듈 | 패키지 루트 | 역할 |
|------|-------------|------|
| `common` | `com.insightboard.common` | 공유 커널 (예외 등) |
| `api` | `com.insightboard.api` | CBoard 메타·대시보드 API |
| `external` | `com.insightboard.external` | DataProvider(JDBC) 등 외부 어댑터 |
| `app` | `com.insightboard.app` | Boot 앱, Security, `application.yml` |

`api` 모듈 내부 레이어:

- `domain` — 엔티티, 포트(interface)
- `application` — 유스케이스·서비스
- `infrastructure` — JPA Repository 등
- `presentation` — REST Controller

## 실행

```bash
./gradlew :modules:app:bootRun
```

## 테스트

```bash
./gradlew test
```
