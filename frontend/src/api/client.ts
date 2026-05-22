import { saveSessionId, sessionAuthHeaders } from '../auth/sessionCookie'

export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

type RequestOptions = {
  method?: string
  body?: unknown
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    ...sessionAuthHeaders(),
  }
  let body: string | undefined
  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
    body = JSON.stringify(options.body)
  }

  const res = await fetch(path, {
    method: options.method ?? 'GET',
    headers,
    body,
    credentials: 'include',
  })

  if (res.status === 401) {
    throw new ApiError('로그인이 필요합니다.', 401)
  }
  if (!res.ok) {
    const text = await res.text()
    let message = text || `HTTP ${res.status}`
    try {
      const parsed = JSON.parse(text) as { message?: string }
      if (parsed.message) message = parsed.message
    } catch {
      /* plain text */
    }
    throw new ApiError(message, res.status)
  }

  if (res.status === 204) {
    return undefined as T
  }
  return (await res.json()) as T
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body?: unknown) => request<T>(path, { method: 'POST', body }),
  put: <T>(path: string, body?: unknown) => request<T>(path, { method: 'PUT', body }),
  delete: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
}

/** 로그인 성공 시 sessionId 저장 (LoginResponse) */
export function applyLoginSession<T extends { sessionId?: string | null }>(data: T): T {
  if (data.sessionId) {
    saveSessionId(data.sessionId)
  }
  return data
}

/** 세션 복원 — 204/401 은 미로그인 */
export async function fetchSession<T extends { sessionId?: string | null }>(): Promise<T | null> {
  const res = await fetch('/api/v1/auth/session', {
    method: 'GET',
    headers: sessionAuthHeaders(),
    credentials: 'include',
  })
  if (res.status === 204 || res.status === 401 || res.status === 403) {
    return null
  }
  if (!res.ok) {
    const text = await res.text()
    let message = text || `HTTP ${res.status}`
    try {
      const parsed = JSON.parse(text) as { message?: string }
      if (parsed.message) message = parsed.message
    } catch {
      /* plain text */
    }
    throw new ApiError(message, res.status)
  }
  const data = (await res.json()) as T
  if (data.sessionId) {
    saveSessionId(data.sessionId)
  }
  return data
}
