# CBoard 100% 내재화 로드맵

## 완료 (이번 작업 — P0 데이터 플레인)

| 항목 | 상태 |
|------|------|
| `getBoardData` 레이아웃 위젯 하이드레이션 | ✅ |
| `getAggregateData` / `getColumns` / `getDimensionValues` | ✅ (H2 시드 기반) |
| `test` 데이터소스 연결 | ✅ |
| `getDatasourceParams` / `getDatasourceView` | ✅ |
| `dashboardWidget`, `getAllWidgetList` | ✅ |
| `checkWidget` / `checkDatasource` | ✅ |
| `getBoardParam` / `saveBoardParam` | ✅ |
| GET+POST `@RequestMapping` (레거시 `$http.post` 호환) | ✅ |
| React 대시보드 뷰 — 하이드레이션된 `widget.data` 사용 | ✅ |

## 다음 우선순위 (P1)

1. **AggConfig 파싱** — `cfg` JSON 기반 실제 집계 (GROUP BY, measure)
2. **JDBC DataProvider** — 사용자 SQL / 테이블 쿼리
3. **`/cboard/admin/*`** — 사용자·역할·RBAC
4. **Job** — `saveJob`, `execJob`, 메일 스냅샷 (`commons/persist`)
5. **보드 빌더** — 드래그앤드롭, `dashboardWidget` 미리보기

## P2

- `exportBoard` / `tableToxls`
- `viewAggDataQuery` (SQL 디버그)
- Kylin / Elasticsearch 등 추가 프로바이더 (BDP에서 실제 쓰는 것만)

## P3

- 메뉴 DB + 역할 필터 (`getMenuList`)
- `edit`/`delete` 플래그 RBAC 연동

자세한 갭 목록: 레거시 `org.cboard.controller.DashboardController` 48 endpoints 대비 `CboardDashboardController`.
