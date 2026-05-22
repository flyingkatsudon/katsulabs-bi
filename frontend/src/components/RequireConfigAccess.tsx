import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthOutletContext } from '../hooks/useAuthOutletContext'
import { canAccessConfiguration } from '../utils/permissions'

/** Viewer 등 설정 화면 접근 불가 역할은 홈으로 돌린다 */
export function RequireConfigAccess({ children }: { children: ReactNode }) {
  const { roleId } = useAuthOutletContext()
  if (!canAccessConfiguration(roleId)) {
    return <Navigate to="/" replace />
  }
  return children
}
