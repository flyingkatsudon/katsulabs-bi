import { useOutletContext } from 'react-router-dom'
import type { AuthOutletContext } from '../auth/AuthOutletContext'
import { DEFAULT_AUTH_OUTLET_CONTEXT } from '../auth/defaultAuthOutletContext'

/** Outlet context 미전달 시에도 크래시 없이 기본 권한 값 사용 */
export function useAuthOutletContext(): AuthOutletContext {
  return useOutletContext<AuthOutletContext | undefined>() ?? DEFAULT_AUTH_OUTLET_CONTEXT
}
