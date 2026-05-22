import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { api, ApiError } from '../../api/client'
import type { ServiceResult, UserSummary } from '../../api/types'
import { FormAlerts } from '../../components/FormAlerts'
import { ROLE_MANAGER, ROLE_SUPER_ADMIN, ROLE_VIEWER } from '../../utils/permissions'

const ROLES = [
  { id: ROLE_SUPER_ADMIN, label: 'Super Admin' },
  { id: ROLE_MANAGER, label: 'Manager' },
  { id: ROLE_VIEWER, label: 'Viewer' },
]

type UsersPageProps = {
  onSessionExpired: () => void
}

export function UsersPage({ onSessionExpired }: UsersPageProps) {
  const [users, setUsers] = useState<UserSummary[]>([])
  const [error, setError] = useState<string | null>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState<UserSummary | null>(null)
  const [form, setForm] = useState({
    userId: '',
    loginName: '',
    displayName: '',
    roleId: ROLE_VIEWER,
    password: '',
  })

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setUsers(await api.get<UserSummary[]>('/api/v1/users'))
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        onSessionExpired()
        return
      }
      setError(e instanceof Error ? e.message : '사용자 목록 조회 실패')
    } finally {
      setLoading(false)
    }
  }, [onSessionExpired])

  useEffect(() => {
    void load()
  }, [load])

  function startCreate() {
    setEditing(null)
    setForm({
      userId: '',
      loginName: '',
      displayName: '',
      roleId: ROLE_VIEWER,
      password: '',
    })
    setMessage(null)
    setError(null)
  }

  function startEdit(u: UserSummary) {
    setEditing(u)
    setForm({
      userId: u.userId,
      loginName: u.loginName,
      displayName: u.displayName,
      roleId: u.roleId,
      password: '',
    })
    setMessage(null)
    setError(null)
  }

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setMessage(null)
    try {
      const body = {
        userId: form.userId,
        loginName: form.loginName || form.userId,
        displayName: form.displayName || form.userId,
        roleId: form.roleId,
        password: form.password || undefined,
      }
      let res: ServiceResult
      if (editing) {
        res = await api.put<ServiceResult>(`/api/v1/users/${editing.userId}`, body)
      } else {
        res = await api.post<ServiceResult>('/api/v1/users', body)
      }
      setMessage(res.message)
      await load()
      if (!editing) startCreate()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        onSessionExpired()
        return
      }
      setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(userId: string) {
    if (!window.confirm(`${userId} 사용자를 비활성화할까요?`)) return
    setError(null)
    try {
      const res = await api.delete<ServiceResult>(`/api/v1/users/${userId}`)
      setMessage(res.message)
      await load()
      if (editing?.userId === userId) startCreate()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        onSessionExpired()
        return
      }
      setError(err instanceof Error ? err.message : '삭제 실패')
    }
  }

  return (
    <>
      <section className="content-header">
        <h1>
          Users <small>사용자 관리 (Super Admin)</small>
        </h1>
      </section>
      <section className="content">
        <FormAlerts message={message} error={error} />
        <div className="row">
          <div className="col-md-7">
            <div className="box box-primary">
              <div className="box-header with-border">
                <h3 className="box-title">사용자 목록</h3>
                <div className="box-tools">
                  <button type="button" className="btn btn-sm btn-primary" onClick={startCreate}>
                    <i className="fa fa-plus" /> 신규
                  </button>
                </div>
              </div>
              <div className="box-body table-responsive">
                {loading && <p className="text-muted">로딩 중…</p>}
                {!loading && (
                  <table className="table table-bordered table-striped">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>이름</th>
                        <th>역할</th>
                        <th />
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((u) => (
                        <tr key={u.userId}>
                          <td>{u.userId}</td>
                          <td>{u.displayName}</td>
                          <td>{u.roleName ?? u.roleId}</td>
                          <td>
                            <button
                              type="button"
                              className="btn btn-xs btn-default"
                              onClick={() => startEdit(u)}
                            >
                              편집
                            </button>
                            {u.userId !== 'admin01' && (
                              <button
                                type="button"
                                className="btn btn-xs btn-danger"
                                style={{ marginLeft: 4 }}
                                onClick={() => void handleDelete(u.userId)}
                              >
                                비활성
                              </button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            </div>
          </div>
          <div className="col-md-5">
            <div className="box box-success">
              <div className="box-header">
                <h3 className="box-title">{editing ? '사용자 수정' : '사용자 등록'}</h3>
              </div>
              <form className="box-body" onSubmit={(e) => void handleSave(e)}>
                <div className="form-group">
                  <label>사용자 ID (6~10자)</label>
                  <input
                    className="form-control"
                    value={form.userId}
                    disabled={!!editing}
                    onChange={(e) => setForm((f) => ({ ...f, userId: e.target.value }))}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>표시 이름</label>
                  <input
                    className="form-control"
                    value={form.displayName}
                    onChange={(e) => setForm((f) => ({ ...f, displayName: e.target.value }))}
                  />
                </div>
                <div className="form-group">
                  <label>역할</label>
                  <select
                    className="form-control"
                    value={form.roleId}
                    onChange={(e) => setForm((f) => ({ ...f, roleId: e.target.value }))}
                  >
                    {ROLES.map((r) => (
                      <option key={r.id} value={r.id}>
                        {r.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>{editing ? '새 비밀번호 (변경 시만)' : '비밀번호'}</label>
                  <input
                    type="password"
                    className="form-control"
                    value={form.password}
                    onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))}
                    required={!editing}
                  />
                </div>
                <button type="submit" className="btn btn-success">
                  저장
                </button>
              </form>
            </div>
            <p className="text-muted">
              데모: <code>viewer01</code> / <code>manager01</code> / <code>admin01</code> — 비밀번호{' '}
              <code>admin123</code>
            </p>
          </div>
        </div>
      </section>
    </>
  )
}
