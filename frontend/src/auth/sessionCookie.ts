/** Vite dev: Set-Cookie 미저장 + fetch 에서 Cookie 헤더 설정 불가 → 커스텀 헤더로 세션 ID 전달 */
const STORAGE_KEY = 'insightboard.jsessionid'

/** @see SessionHeaderRegistry.SESSION_HEADER (backend) */
export const SESSION_HEADER = 'X-Insightboard-Session'

export function saveSessionId(sessionId: string | null | undefined): void {
  if (sessionId) {
    sessionStorage.setItem(STORAGE_KEY, sessionId)
  } else {
    sessionStorage.removeItem(STORAGE_KEY)
  }
}

export function getSessionId(): string | null {
  return sessionStorage.getItem(STORAGE_KEY)
}

export function clearSessionId(): void {
  sessionStorage.removeItem(STORAGE_KEY)
}

export function sessionAuthHeaders(): Record<string, string> {
  const id = getSessionId()
  if (!id) return {}
  return { [SESSION_HEADER]: id }
}
