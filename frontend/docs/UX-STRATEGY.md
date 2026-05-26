# UI/UX 마이그레이션 원칙

## 목표

| 계층 | 방향 |
|------|------|
| **로직·상태** | React + Spring Boot `/api/v1/*` |
| **스타일** | 레거시 AdminLTE CSS (`katsulabs-bi/`) (AdminLTE, `katsulabs-bi.css`, 플러그인) — `index.html` + `sync-katsulabs-bi-assets.sh` |
| **메뉴·레이아웃** | `starter.jsp` / `main-sidebar.html` / `main-header.html` 과 동일한 AdminLTE shell |
| **상세 화면 UX** | `org/insightboard/view/config/*.html` 패턴을 React 컴포넌트로 **재현** (코드 복사 X) |

즉 **코드는 React**, **보이는 것·조작 흐름은 레거시**.

## 구현 매핑

```
starter.jsp          → AppLayout + AppHeader + AppSidebar
ui-view              → React Router <Outlet /> (section.content)
config/datasource    → DatasourceWorkbenchPage + ConfigWorkbench (col-md-3/9)
config/dataset       → DatasetWorkbenchPage (jsTree, Load Data, 스키마 빌더)
config/widget        → WidgetWorkbenchPage (jsTree, 스키마 패널, 차트별 Row/Column/Value)
config/board         → BoardWorkbenchPage (jsTree, grid/timeline 행·위젯 슬롯)
mine/{id}            → BoardViewPage (대시보드 보기, ECharts)
config/category      → CategoryPage (목록)
```

## 복잡 화면 (위젯 차트, 보드 그리드)

- **차트**: ECharts + `echarts-wordcloud` — line/pie/sankey/wordCloud/treeMap 등 React 렌더. 지도·Fusion 등은 표 + 레거시 편집기 링크.
- **보드**: `grid` / `timeline` / **`free`** (47×47 드래그 캔버스, `layout_json.widgets[]`).
- **위젯**: `series_type` (stack bar, area line, …) — line/bar 계열.
- **카테고리**: `POST/PUT/DELETE /api/v1/categories` + Configuration 화면 CRUD.

## 정적 UI 자산

AdminLTE·Font Awesome·플러그인은 **`frontend/public/katsulabs-bi/`** 에 vendored 되어 있으며, 앱은 `/katsulabs-bi/...` URL로 로드합니다 (레거시 WAR `webapp` 제거 후 이 경로가 단일 소스).

별도 「동기화」는 로컬 개발에 **필수가 아닙니다**. upstream 레거시 WAR에서 CSS/JS를 다시 받아와야 할 때만 `sync-katsulabs-bi-assets.sh` 를 사용합니다.
