import { useState } from 'react'
import type { FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, ApiError, applyLoginSession } from '../api/client'
import type { LoginResponse } from '../api/types'
import { BRAND_LOGO_HTML, BRAND_ORG, BRAND_APP } from '../brand'

type LoginPageProps = {
  onLogin: (user: LoginResponse, password: string) => void | Promise<void>
  /** 로그인 후 이동 경로 (기본 / → Demo Board 보기) */
  redirectTo?: string
}

export function LoginPage({ onLogin, redirectTo = '/' }: LoginPageProps) {
  const navigate = useNavigate()
  const [userId, setUserId] = useState('admin01')
  const [password, setPassword] = useState('admin123')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const data = applyLoginSession(
        await api.post<LoginResponse>('/api/v1/auth/login', { userId, password }),
      )
      await onLogin(data, password)
      if (data.sessionId) {
        navigate(redirectTo, { replace: true })
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
      } else {
        setError(err instanceof Error ? err.message : '로그인 오류')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-box">
      <div
        className="login-logo"
        dangerouslySetInnerHTML={{ __html: BRAND_LOGO_HTML }}
        aria-label={`${BRAND_ORG} ${BRAND_APP}`}
      />
      <div className="login-box-body">
        <p className="login-box-msg">로그인</p>
        <form onSubmit={handleSubmit}>
          <div className="form-group has-feedback">
            <input
              type="text"
              className="form-control"
              placeholder="사용자 ID"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              autoComplete="username"
            />
            <span className="glyphicon glyphicon-user form-control-feedback" />
          </div>
          <div className="form-group has-feedback">
            <input
              type="password"
              className="form-control"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
            <span className="glyphicon glyphicon-lock form-control-feedback" />
          </div>
          {error && <p className="text-danger">{error}</p>}
          <button type="submit" className="btn btn-primary btn-block btn-flat" disabled={loading}>
            {loading ? '로그인 중…' : '로그인'}
          </button>
        </form>
      </div>
    </div>
  )
}
