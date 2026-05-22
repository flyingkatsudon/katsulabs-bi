import { Navigate, Outlet } from 'react-router-dom'
import type { LoginResponse } from '../api/types'
import type { AuthOutletContext } from '../auth/AuthOutletContext'
import { AppLayout } from './AppLayout'

type ProtectedLayoutProps = {
  bootstrapped: boolean
  isRestoringSession: boolean
  user: LoginResponse | null
  onLogout: () => void
  outletContext: AuthOutletContext
}

const PLACEHOLDER_USER: LoginResponse = {
  userId: '…',
  userName: '',
  roleId: '2',
  roleName: '',
}

/** 세션 복원 중에도 현재 URL·레이아웃 유지 */
export function ProtectedLayout({
  bootstrapped,
  isRestoringSession,
  user,
  onLogout,
  outletContext,
}: ProtectedLayoutProps) {
  if (bootstrapped && !user?.userId) {
    return <Navigate to="/login" replace />
  }

  const layoutUser = user ?? PLACEHOLDER_USER

  return (
    <AppLayout
      user={layoutUser}
      onLogout={onLogout}
      isRestoringSession={isRestoringSession}
    >
      <Outlet context={outletContext} />
    </AppLayout>
  )
}
