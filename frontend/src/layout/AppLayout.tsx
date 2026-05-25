import type { ReactNode } from 'react'
import type { LoginResponse } from '../api/types'
import { AppHeader } from './AppHeader'
import { AppSidebar } from './AppSidebar'

type AppLayoutProps = {
  user: LoginResponse
  onLogout?: () => void
  isRestoringSession?: boolean
  children: ReactNode
}

/** 레거시 starter.jsp — wrapper + header + sidebar + content-wrapper (ui-view) */
export function AppLayout({ user, onLogout, isRestoringSession = false, children }: AppLayoutProps) {
  return (
    <div className="wrapper">
      <AppHeader
        userId={user.userId}
        roleName={user.roleName}
        onLogout={onLogout}
        isRestoringSession={isRestoringSession}
      />
      <AppSidebar user={user} dimmed={isRestoringSession} />
      <div className="content-wrapper">{children}</div>
    </div>
  )
}
