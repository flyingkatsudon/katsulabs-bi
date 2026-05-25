import { useCallback, useEffect, useState } from 'react'
import { fetchSession } from '../api/client'
import type { LoginResponse } from '../api/types'

export type AuthStatus = 'loading' | 'authenticated' | 'anonymous'

export function normalizeLogin(data: LoginResponse): LoginResponse {
  return {
    ...data,
    userName: data.userName || data.displayName || data.loginName || data.userId,
    roleId: data.roleId != null ? String(data.roleId) : '2',
    roleName: data.roleName ?? (data.roleId != null ? String(data.roleId) : 'Viewer'),
  }
}

export function useAuthSession() {
  const [bootstrapped, setBootstrapped] = useState(false)
  const [status, setStatus] = useState<AuthStatus>('loading')
  const [user, setUser] = useState<LoginResponse | null>(null)

  const restoreSession = useCallback(async () => {
    const session = await fetchSession()
    if (session) {
      setUser(normalizeLogin(session))
      setStatus('authenticated')
    } else {
      setUser(null)
      setStatus('anonymous')
    }
    setBootstrapped(true)
  }, [])

  useEffect(() => {
    void restoreSession()
  }, [restoreSession])

  const setAuthenticated = useCallback((auth: LoginResponse) => {
    setUser(normalizeLogin(auth))
    setStatus('authenticated')
  }, [])

  const clearAuth = useCallback(() => {
    setUser(null)
    setStatus('anonymous')
  }, [])

  const isRestoringSession = !bootstrapped

  return {
    status,
    user,
    bootstrapped,
    isRestoringSession,
    restoreSession,
    setAuthenticated,
    clearAuth,
  }
}
