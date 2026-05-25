# Insight Board — React 프론트

## UI/UX 원칙

**로직은 React**, **CSS·메뉴·화면 구성은 AdminLTE 스타일** (`frontend/public/insightboard/`). 브랜드: **Katsulabs | Insight Board**.

- Shell: `AppLayout` / `AppHeader` / `AppSidebar`
- Configuration: DataSource, Dataset, Widget, Board, Category workbench
- API: Boot `/api/v1/*`

자세한 매핑: [docs/UX-STRATEGY.md](docs/UX-STRATEGY.md)

## 실행

```bash
./gradlew :modules:api:bootRun --args='--spring.profiles.active=local-h2'

cd frontend
npm install
npm run dev
```

→ http://localhost:5173 — **admin01** / **admin123**

Vite는 `/api` 를 `http://localhost:8081` 로 프록시합니다.

## 위젯 차트

- Configuration → Widget: Row/Column/Value 설정 후 **Preview**
- 차트 유형별 안내: 편집 화면 파란 **차트 설정 가이드** 박스·tooltip
- 대시보드: 위젯 헤더 **새로고침** / **편집(렌치)**

## 정적 자산

AdminLTE·플러그인은 `public/insightboard/` 에 포함됩니다. 외부 CBoard 배포본에서 갱신할 때:

```bash
bash frontend/scripts/sync-cboard-assets.sh /path/to/webapp/cboard
```
