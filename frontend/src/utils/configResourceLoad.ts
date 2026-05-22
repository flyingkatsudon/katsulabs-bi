import { ApiError } from '../api/client'
import type { ConfigLoadIssue } from '../components/config/ConfigEditorPane'
import { handleApiError } from './handleApiError'

/** Configuration 상세 로드 catch — 404·401 분리 */
export function resolveConfigLoadError(
  e: unknown,
  onSessionExpired: () => void,
): ConfigLoadIssue | 'error' {
  let issue: ConfigLoadIssue | null = null
  const handled = handleApiError(e, {
    onSessionExpired,
    onNotFound: () => {
      issue = 'not_found'
    },
  })
  if (handled && issue) return issue
  if (handled) return 'error'
  return 'error'
}

export function isNotFoundError(e: unknown): boolean {
  return e instanceof ApiError && e.status === 404
}
