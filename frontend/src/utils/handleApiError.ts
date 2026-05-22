import { ApiError } from '../api/client'

type ApiErrorHandlers = {
  onSessionExpired: () => void
  onNotFound?: () => void
  setError?: (message: string) => void
  fallbackMessage?: string
}

/** API 오류를 케이스별로 처리. 처리했으면 true */
export function handleApiError(e: unknown, handlers: ApiErrorHandlers): boolean {
  if (e instanceof ApiError && e.status === 401) {
    handlers.onSessionExpired()
    return true
  }
  if (e instanceof ApiError && e.status === 404 && handlers.onNotFound) {
    handlers.onNotFound()
    return true
  }
  if (handlers.setError) {
    handlers.setError(
      e instanceof Error ? e.message : handlers.fallbackMessage ?? '요청 처리에 실패했습니다.',
    )
  }
  return false
}
