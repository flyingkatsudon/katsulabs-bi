import { useCallback, useEffect, useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes, useParams } from 'react-router-dom'
import { api, ApiError } from './api/client'
import { clearSessionId, getSessionId } from './auth/sessionCookie'
import type { BoardSummary, LoginResponse } from './api/types'
import {
  canManageUsers,
  canWriteDashboard,
  canWriteDatasource,
} from './utils/permissions'
import { useAuthSession } from './auth/useAuthSession'
import { CboardBodyClassSync } from './legacy/CboardBodyClassSync'
import { RouteErrorBoundary } from './components/RouteErrorBoundary'
import { ProtectedLayout } from './layout/ProtectedLayout'
import { HomeRedirect } from './components/HomeRedirect'
import { BoardViewPage } from './pages/BoardViewPage'
import { BoardWorkbenchPage } from './pages/config/BoardWorkbenchPage'
import { CategoryPage } from './pages/config/CategoryPage'
import { DatasetWorkbenchPage } from './pages/config/DatasetWorkbenchPage'
import { DatasourceWorkbenchPage } from './pages/config/DatasourceWorkbenchPage'
import { WidgetWorkbenchPage } from './pages/config/WidgetWorkbenchPage'
import { LoginPage } from './pages/LoginPage'
import { UsersPage } from './pages/config/UsersPage'

function RedirectConfigId({ base }: { base: string }) {
  const { id } = useParams()
  return <Navigate to={`${base}?id=${id ?? ''}`} replace />
}

function AppRoutes() {
  const { status, user, bootstrapped, isRestoringSession, setAuthenticated, clearAuth } =
    useAuthSession()
  const [boards, setBoards] = useState<BoardSummary[]>([])
  const [, setBoardsLoading] = useState(false)
  const [, setBoardsError] = useState<string | null>(null)

  const handleUnauthorized = useCallback(() => {
    if (!bootstrapped) return
    clearSessionId()
    clearAuth()
    setBoards([])
  }, [bootstrapped, clearAuth])

  const loadBoards = useCallback(async () => {
    setBoardsLoading(true)
    setBoardsError(null)
    try {
      setBoards(await api.get<BoardSummary[]>('/api/v1/boards'))
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        handleUnauthorized()
        return
      }
      setBoardsError(e instanceof Error ? e.message : '목록 조회 실패')
    } finally {
      setBoardsLoading(false)
    }
  }, [handleUnauthorized])

  const handleLogin = useCallback(
    async (auth: LoginResponse) => {
      setAuthenticated(auth)
      if (getSessionId()) {
        await loadBoards()
      }
    },
    [setAuthenticated, loadBoards],
  )

  const handleLogout = useCallback(() => {
    void api.post('/api/v1/auth/logout').catch(() => {
      /* 세션 없어도 클라이언트 상태는 초기화 */
    })
    clearSessionId()
    clearAuth()
    setBoards([])
  }, [clearAuth])

  useEffect(() => {
    if (status === 'authenticated' && user && getSessionId()) {
      void loadBoards()
    }
  }, [status, user, loadBoards])

  const outletContext =
    user && status === 'authenticated'
      ? {
          roleId: user.roleId,
          canWriteDashboard: canWriteDashboard(user.roleId),
          canWriteDatasource: canWriteDatasource(user.roleId),
          canManageUsers: canManageUsers(user.roleId),
          bootstrapped,
        }
      : bootstrapped
        ? { roleId: '2', canWriteDashboard: false, canWriteDatasource: false, canManageUsers: false, bootstrapped }
        : undefined

  return (
    <>
      <CboardBodyClassSync />
      <Routes>
        <Route
          path="/login"
          element={
            !bootstrapped ? (
              <div className="login-box" style={{ margin: '10% auto' }}>
                <p className="text-center text-muted">
                  <i className="fa fa-refresh fa-spin" /> 새로고침 중입니다
                </p>
              </div>
            ) : status === 'authenticated' && user ? (
              <Navigate to="/" replace />
            ) : (
              <LoginPage onLogin={handleLogin} />
            )
          }
        />
        <Route
          element={
            <RouteErrorBoundary>
              <ProtectedLayout
                bootstrapped={bootstrapped}
                isRestoringSession={isRestoringSession}
                user={user}
                onLogout={handleLogout}
                outletContext={outletContext}
              />
            </RouteErrorBoundary>
          }
        >
          <Route
            index
            element={
              !bootstrapped ? null : status === 'authenticated' && user ? (
                <HomeRedirect userId={user.userId} />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
          <Route path="/mine/:id" element={<BoardViewPage onUnauthorized={handleUnauthorized} />} />
          <Route
            path="/boards"
            element={
              <BoardWorkbenchPage
                boards={boards}
                onUnauthorized={handleUnauthorized}
                onBoardsChange={() => void loadBoards()}
              />
            }
          />
          <Route path="/boards/:id" element={<RedirectConfigId base="/boards" />} />
          <Route path="/datasources" element={<DatasourceWorkbenchPage onUnauthorized={handleUnauthorized} />} />
          <Route path="/datasources/:id" element={<RedirectConfigId base="/datasources" />} />
          <Route path="/datasets" element={<DatasetWorkbenchPage onUnauthorized={handleUnauthorized} />} />
          <Route path="/datasets/:id" element={<RedirectConfigId base="/datasets" />} />
          <Route path="/widgets" element={<WidgetWorkbenchPage onUnauthorized={handleUnauthorized} />} />
          <Route path="/widgets/:id" element={<RedirectConfigId base="/widgets" />} />
          <Route path="/categories" element={<CategoryPage onUnauthorized={handleUnauthorized} />} />
          <Route path="/users" element={<UsersPage onUnauthorized={handleUnauthorized} />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  )
}
