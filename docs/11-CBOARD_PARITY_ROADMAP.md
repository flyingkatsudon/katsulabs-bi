# CBoard 100% 내재화 로드맵

## P0 — 데이터 플레인 ✅

| 항목 | 상태 |
|------|------|
| `getBoardData` 위젯 하이드레이션 | ✅ |
| `getAggregateData` / `getColumns` / `getDimensionValues` / `test` | ✅ |
| GET+POST `@RequestMapping` | ✅ |

## P1 — AggConfig + JDBC 집계 ✅

| 항목 | 상태 |
|------|------|
| `ViewAggConfig` / `AggConfig` 파싱 | ✅ |
| `JdbcAggregateQueryBuilder` (GROUP BY, SUM/COUNT/…) | ✅ |
| 데이터셋 `query.table` / `query.sql` 해석 | ✅ |
| `viewAggDataQuery` (SQL 디버그) | ✅ |
| 테스트: `JdbcAggregateQueryBuilderTest`, agg cfg 통합 테스트 | ✅ |

## P2 — Admin / RBAC ✅

| 항목 | 상태 |
|------|------|
| `dashboard_role`, `dashboard_user_role`, `dashboard_role_res` JPA | ✅ |
| `/cboard/admin/*` (`CboardAdminController`) | ✅ |
| `isAdmin` / `isConfig`, 사용자·역할 CRUD | ✅ |
| 시드: ADMIN 역할 + admin 사용자 | ✅ |
| 테스트: `CboardAdminTest` | ✅ |

## P3 — Job · Export · 보드 빌더 ✅ (기본)

| 항목 | 상태 |
|------|------|
| `dashboard_job` + Job CRUD / `execJob` | ✅ |
| `exportBoard` / `tableToxls` (TSV 바이트) | ✅ |
| React 보드 설정: 드래그 순서 변경 | ✅ |
| 대시보드 Export 버튼 | ✅ |
| 테스트: `CboardJobExportTest` | ✅ |

## 남은 고도화 (100% 완전 동등 이전)

- 실제 POI XLS, 메일 Job 실행, `commons/persist` 스냅샷
- Kylin / ES 등 추가 DataProvider
- Ace/OLAP 위젯·데이터셋 편집기
- 메뉴 DB + 역할별 `getMenuList` 필터
- 레거시 보드 파라미터 UI (`param` row)

**총 백엔드 테스트:** 17개 (`./gradlew test`)
