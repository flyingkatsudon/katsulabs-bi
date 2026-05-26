import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
  useLocation,
  useNavigate,
  useParams,
} from 'react-router-dom'
import { ContentLoading } from './components/ContentLoading'
import { api, ApiError } from './api/client'
import type { AuthOutletContext } from './auth/AuthOutletContext'
import { DEFAULT_AUTH_OUTLET_CONTEXT } from './auth/defaultAuthOutletContext'
import { clearSessionId, getSessionId } from './auth/sessionCookie'
import type { BoardSummary, LoginResponse } from './api/types'
import {
  canManageUsers,
  canWriteDashboard,
  canWriteDatasource,
} from './utils/permissions'
import { useAuthSession } from './auth/useAuthSession'
import { LegacyBodyClassSync } from './legacy/LegacyBodyClassSync'
import { RouteErrorBoundary } from './components/RouteErrorBoundary'
import { SessionExpiredDialog } from './components/SessionExpiredDialog'
import { ProtectedLayout } from './layout/ProtectedLayout'
import { HomeRedirect } from './components/HomeRedirect'
import { RequireConfigAccess } from './components/RequireConfigAccess'
import { BoardViewPage } from './pages/BoardViewPage'
import { NotFoundPage } from './pages/NotFoundPage'
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
  const navigate = useNavigate()
  const location = useLocation()
  const { status, user, bootstrapped, isRestoringSession, setAuthenticated, clearAuth } =
    useAuthSession()
  const [boards, setBoards] = useState<BoardSummary[]>([])
  const [, setBoardsLoading] = useState(false)
  const [, setBoardsError] = useState<string | null>(null)
  const [sessionExpiredOpen, setSessionExpiredOpen] = useState(false)

  const handleSessionExpired = useCallback(() => {
    if (!bootstrapped) return
    clearSessionId()
    setSessionExpiredOpen(true)
  }, [bootstrapped])

  const confirmSessionExpired = useCallback(() => {
    setSessionExpiredOpen(false)
    clearAuth()
    setBoards([])
    navigate('/login', { replace: true })
  }, [clearAuth, navigate])

  const loadBoards = useCallback(async () => {
    setBoardsLoading(true)
    setBoardsError(null)
    try {
      setBoards(await api.get<BoardSummary[]>('/api/v1/boards'))
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        handleSessionExpired()
        return
      }
      setBoardsError(e instanceof Error ? e.message : '목록 조회 실패')
    } finally {
      setBoardsLoading(false)
    }
  }, [handleSessionExpired])

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

  const outletContext: AuthOutletContext = useMemo(
    () =>
      user && status === 'authenticated'
        ? {
            roleId: user.roleId,
            canWriteDashboard: canWriteDashboard(user.roleId),
            canWriteDatasource: canWriteDatasource(user.roleId),
            canManageUsers: canManageUsers(user.roleId),
            bootstrapped,
          }
        : { ...DEFAULT_AUTH_OUTLET_CONTEXT, bootstrapped },
    [user, status, bootstrapped],
  )

  return (
    <>
      <LegacyBodyClassSync />
      <SessionExpiredDialog open={sessionExpiredOpen} onConfirm={confirmSessionExpired} />
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
            <RouteErrorBoundary
              resetKey={location.pathname}
              onGoHome={() => navigate('/', { replace: true })}
            >
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
              !bootstrapped ? (
                <ContentLoading message="세션 확인 중…" />
              ) : status === 'authenticated' && user ? (
                <HomeRedirect user={user} onSessionExpired={handleSessionExpired} />
              ) : (
                <Navigate to="/login" replace />
              )
            }
          />
          <Route path="/mine/:id" element={<BoardViewPage onSessionExpired={handleSessionExpired} />} />
          <Route
            path="/boards"
            element={
              <RequireConfigAccess>
                <BoardWorkbenchPage
                  boards={boards}
                  onSessionExpired={handleSessionExpired}
                  onBoardsChange={() => void loadBoards()}
                />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/boards/:id"
            element={
              <RequireConfigAccess>
                <RedirectConfigId base="/boards" />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/datasources"
            element={
              <RequireConfigAccess>
                <DatasourceWorkbenchPage onSessionExpired={handleSessionExpired} />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/datasources/:id"
            element={
              <RequireConfigAccess>
                <RedirectConfigId base="/datasources" />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/datasets"
            element={
              <RequireConfigAccess>
                <DatasetWorkbenchPage onSessionExpired={handleSessionExpired} />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/datasets/:id"
            element={
              <RequireConfigAccess>
                <RedirectConfigId base="/datasets" />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/widgets"
            element={
              <RequireConfigAccess>
                <WidgetWorkbenchPage onSessionExpired={handleSessionExpired} />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/widgets/:id"
            element={
              <RequireConfigAccess>
                <RedirectConfigId base="/widgets" />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/categories"
            element={
              <RequireConfigAccess>
                <CategoryPage onSessionExpired={handleSessionExpired} />
              </RequireConfigAccess>
            }
          />
          <Route
            path="/users"
            element={
              <RequireConfigAccess>
                <UsersPage onSessionExpired={handleSessionExpired} />
              </RequireConfigAccess>
            }
          />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
        <Route
          path="*"
          element={bootstrapped && status === 'authenticated' ? <Navigate to="/" replace /> : <Navigate to="/login" replace />}
        />
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
