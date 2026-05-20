# BDP Next — Spring Boot + React (CBoard UI)

레거시 CBoard(AdminLTE **skin-blue**) 화면을 React로 재구현한 스택입니다.

## 실행 (2터미널)

**터미널 1 — API**

```bash
cd bdp-next/backend
./gradlew bootRun
```

**터미널 2 — UI**

```bash
cd bdp-next/frontend
npm install
npm run dev
```

브라우저: http://localhost:5173  
로그인: `admin` / `admin`

## 포함 화면 (레거시 동일 레이아웃)

| 화면 | 경로 |
|------|------|
| 로그인 (Shinhan \| BDP) | `/login` |
| 홈 / Cubes | `/` |
| 대시보드 보기 (ECharts 위젯) | `/dashboard/category/:catId/:id`, `/mine/:id` |
| Config — DataSource | `/config/datasource` |
| Config — Dataset | `/config/dataset` |
| Config — Widget | `/config/widget` |
| Config — Board | `/config/board` |
| Config — Category | `/config/category` |

API는 레거시와 동일 prefix: `/cboard/dashboard/*`, `/cboard/commons/*` (JWT Bearer)

## 데모 데이터

- 카테고리 **시장 브리핑**
- 보드 **데모 대시보드** (라인 + 파이 차트 위젯 2개)
- H2 in-memory, 재시작 시 시드 재생성 (`ddl-auto: create-drop`)

## 테스트

```bash
cd bdp-next/backend && ./gradlew test
cd bdp-next/frontend && npm run build
```

## 스타일 자산

`frontend/public/cboard/` — 레거시 AdminLTE CSS, Bootstrap 3, Font Awesome (일부 복사)

## 문서

- [../docs/08-REBUILD_DIRECTION_V2.md](../docs/08-REBUILD_DIRECTION_V2.md)
