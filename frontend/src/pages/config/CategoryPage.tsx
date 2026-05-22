import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { api, ApiError } from '../../api/client'
import type { CategoryItem, ServiceResult } from '../../api/types'
import { FormAlerts } from '../../components/FormAlerts'

type CategoryPageProps = {
  onSessionExpired: () => void
}

export function CategoryPage({ onSessionExpired }: CategoryPageProps) {
  const [list, setList] = useState<CategoryItem[]>([])
  const [name, setName] = useState('')
  const [editId, setEditId] = useState<number | null>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setList(await api.get<CategoryItem[]>('/api/v1/categories'))
  }, [])

  useEffect(() => {
    void load().catch((e) => {
      if (e instanceof ApiError && e.status === 401) onSessionExpired()
      else setError(e instanceof Error ? e.message : '로드 실패')
    })
  }, [load, onSessionExpired])

  function startNew() {
    setEditId(null)
    setName('')
    setMessage(null)
    setError(null)
  }

  function startEdit(c: CategoryItem) {
    setEditId(c.id)
    setName(c.name)
    setMessage(null)
    setError(null)
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setMessage(null)
    setError(null)
    try {
      let res: ServiceResult
      if (editId != null) {
        res = await api.put<ServiceResult>(`/api/v1/categories/${editId}`, { name })
      } else {
        res = await api.post<ServiceResult>('/api/v1/categories', { name })
      }
      if (res.status !== '1') {
        setError(res.message)
        return
      }
      setMessage(editId != null ? '수정되었습니다.' : '생성되었습니다.')
      startNew()
      await load()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(id: number) {
    if (!window.confirm('이 카테고리를 삭제할까요?')) return
    setError(null)
    try {
      const res = await api.delete<ServiceResult>(`/api/v1/categories/${id}`)
      if (res.status !== '1') {
        setError(res.message)
        return
      }
      setMessage('삭제되었습니다.')
      if (editId === id) startNew()
      await load()
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '삭제 실패')
    }
  }

  return (
    <div className="row">
      <div className="col-md-5">
        <div className="box box-primary">
          <div className="box-header with-border">
            <h3 className="box-title">{editId != null ? 'Edit Category' : 'New Category'}</h3>
          </div>
          <form onSubmit={(e) => void handleSubmit(e)}>
            <div className="box-body">
              <FormAlerts message={message} error={error} />
              <div className="form-group">
                <label>Name</label>
                <input
                  className="form-control"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>
            </div>
            <div className="box-footer">
              <button type="submit" className="btn btn-primary">
                {editId != null ? 'Update' : 'Create'}
              </button>
              {editId != null && (
                <button type="button" className="btn btn-default" style={{ marginLeft: 8 }} onClick={startNew}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
      <div className="col-md-7">
        <div className="box box-primary">
          <div className="box-header with-border">
            <h3 className="box-title">Categories</h3>
            <div className="box-tools pull-right">
              <button type="button" className="btn btn-success btn-sm" onClick={startNew}>
                <i className="fa fa-plus" /> New
              </button>
            </div>
          </div>
          <div className="box-body">
            <table className="table table-bordered table-hover">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Owner</th>
                  <th style={{ width: 120 }} />
                </tr>
              </thead>
              <tbody>
                {list.map((c) => (
                  <tr key={c.id}>
                    <td>{c.id}</td>
                    <td>{c.name}</td>
                    <td>{c.userId}</td>
                    <td>
                      <button type="button" className="btn btn-xs btn-primary" onClick={() => startEdit(c)}>
                        Edit
                      </button>
                      <button
                        type="button"
                        className="btn btn-xs btn-danger"
                        style={{ marginLeft: 4 }}
                        onClick={() => void handleDelete(c.id)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
