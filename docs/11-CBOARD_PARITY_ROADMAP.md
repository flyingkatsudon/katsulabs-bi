# CBoard 100% 내재화 로드맵

## 완료

| Phase | 내용 |
|-------|------|
| P0 | 데이터 플레인, 보드 하이드레이션, GET+POST |
| P1 | AggConfig + JDBC 집계, viewAggDataQuery |
| P2 | `/cboard/admin` RBAC |
| P3 | Job CRUD, export, 보드 DnD |
| **P4** | `changePwd`, `persist` (`CboardCommonsService`) |
| **P5** | 역할 기반 `getMenuList`, `CboardPermissionService` edit/delete |
| **P6** | 보드 파라미터 UI (`getBoardParam` / `saveBoardParam`) |
| **P7** | 미사용 코드·의존성 정리 (springdoc 제거, ListConfigPage 삭제) |

**백엔드 테스트:** 21개 (`./gradlew test`)

## 의존성 정책

- **추가하지 않음:** Apache POI, Kylin, Elasticsearch, fastjson, 레거시 CBoard JAR
- **제거함:** `springdoc-openapi` (미사용 API 문서)
- **유지:** Spring Boot Web/JPA/Security, JWT, H2, Lombok, Actuator(health)

## 선택적 후속 (완전 동등)

- 실제 메일 발송 Job 스케줄러 (Quartz 등 — 필요 시만)
- Ace/OLAP 편집기 (프론트 전용 라이브러리)
- POI XLS (현재 TSV 바이트로 `.xls` 다운로드 호환)
