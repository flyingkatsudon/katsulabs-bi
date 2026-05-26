# Node.js 마이그레이션 가이드

## 적합한 용도

- **React + Vite** 프론트 (`frontend/`, Node **24+**)
- BFF(Backend for Frontend): 레거시·신규 API 통합, 응답 shape 변환
- WebSocket/SSE 알림, 파일 업로드 프록시

## 런타임

`frontend/package.json`:

```json
"engines": { "node": ">=24.0.0" }
```

## JSON

브라우저·Node 공통: **`fetch` + `JSON.parse` / `JSON.stringify`**

날짜 필드는 API에서 ISO-8601 문자열로 받습니다 (`Instant` 직렬화).

```typescript
type BoardResponse = {
  id: number
  name: string
  layoutJson: string | null
  createdAt: string
}

const res = await fetch('/api/v1/boards', { credentials: 'include' })
const boards: BoardResponse[] = await res.json()
```

서버 사이드 BFF (Express/Fastify) 예:

```typescript
import express from 'express'

const app = express()
app.use('/api', async (req, res) => {
  const upstream = await fetch(`http://localhost:8081${req.url}`, {
    headers: { cookie: req.headers.cookie ?? '' },
  })
  res.status(upstream.status).json(await upstream.json())
})
```

## 인증

1. 브라우저 → Boot `POST /api/v1/auth/login` (`credentials: 'include'`)
2. 세션 쿠키 자동 전달 → `GET /api/v1/boards`

Vite 프록시 (`frontend/vite.config.ts`)가 이미 `/api` → `8081` 로 설정되어 있습니다.

## 레거시 API와 병행

| 용도 | 경로 |
|------|------|
| 신규 | `/api/v1/*` |
| 레거시 | `/bdp/.../dashboard/*` |

BFF에서 신규 API로 점진 전환:

```typescript
// feature flag 예
const boards = useNewApi
  ? await fetch('/api/v1/boards', { credentials: 'include' })
  : await fetch('/bdp/.../dashboard/getBoardList', { credentials: 'include' })
```

## 테스트 (Vitest)

```typescript
import { describe, it, expect } from 'vitest'

describe('Board API types', () => {
  it('parses list', () => {
    const json = [{ id: 1, name: 'Demo Board' }]
    expect(json[0].name).toBe('Demo Board')
  })
})
```

## AdminLTE CSS

- 기존 CSS는 레거시 WAR `webapp/` 정적 경로에 유지
- React 로 이전 시 `frontend/public/katsulabs-bi/` 로 복사 후 `<link>` 또는 `@import`
- **디자인 토큰 변경 없이** 클래스명 유지 권장

## 권장하지 않는 것

- Node 만으로 DataProvider·Kylin 집계 재구현 (Java 코어 유지)
- Fastjson 포트 — 반드시 표준 JSON 또는 `zod` 검증
