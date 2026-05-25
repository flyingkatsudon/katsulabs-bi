import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api, ApiError } from '../api/client'
import type { BoardDetail, CategoryItem, ServiceResult } from '../api/types'
import { FormAlerts } from '../components/FormAlerts'
import { DEFAULT_BOARD_LAYOUT } from '../templates/cboardDefaults'

type BoardDetailPageProps = {
  onUnauthorized: () => void
}

export function BoardDetailPage({ onUnauthorized }: BoardDetailPageProps) {
  const { id } = useParams()
  const navigate = useNavigate()
  const isNew = id === 'new'
  const boardId = isNew ? null : Number(id)

  const [board, setBoard] = useState<BoardDetail | null>(null)
  const [categories, setCategories] = useState<CategoryItem[]>([])
  const [name, setName] = useState('')
  const [categoryId, setCategoryId] = useState<string>('')
  const [layoutJson, setLayoutJson] = useState(DEFAULT_BOARD_LAYOUT)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const cats = await api.get<CategoryItem[]>('/api/v1/categories')
      setCategories(cats)
      if (isNew) {
        setCategoryId(cats[0] ? String(cats[0].id) : '')
        setLayoutJson(DEFAULT_BOARD_LAYOUT)
        setLoading(false)
        return
      }
      if (!Number.isFinite(boardId)) {
        setError('잘못된 보드 ID')
        setLoading(false)
        return
      }
      const detail = await api.get<BoardDetail>(`/api/v1/boards/${boardId}`)
      setBoard(detail)
      setName(detail.name)
      setCategoryId(detail.categoryId != null ? String(detail.categoryId) : '')
      setLayoutJson(detail.layoutJson ?? DEFAULT_BOARD_LAYOUT)
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        onUnauthorized()
        return
      }
      setError(e instanceof Error ? e.message : '로드 실패')
    } finally {
      setLoading(false)
    }
  }, [boardId, isNew, onUnauthorized])

  useEffect(() => {
    void load()
  }, [load])

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    setMessage(null)
    setError(null)
    if (!name.trim()) {
      setError('이름을 입력하세요.')
      return
    }
    try {
      JSON.parse(layoutJson)
    } catch {
      setError('layoutJson 이 유효한 JSON 이 아닙니다.')
      return
    }
    const body = {
      name: name.trim(),
      categoryId: categoryId ? Number(categoryId) : null,
      layoutJson,
    }
    try {
      const result = isNew
        ? await api.post<ServiceResult>('/api/v1/boards', body)
        : await api.put<ServiceResult>(`/api/v1/boards/${boardId}`, body)
      if (result.status === '1') {
        const targetId = isNew && result.id != null ? result.id : boardId
        setMessage('저장되었습니다.')
        if (targetId != null) {
          navigate(`/boards/${targetId}`, { replace: true })
        } else {
          navigate('/boards')
        }
      } else {
        setError(result.message)
      }
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        onUnauthorized()
        return
      }
      setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete() {
    if (isNew || boardId == null) return
    if (!window.confirm('이 대시보드를 삭제할까요?')) return
    try {
      const result = await api.delete<ServiceResult>(`/api/v1/boards/${boardId}`)
      if (result.status === '1') {
        navigate('/boards')
      } else {
        setError(result.message)
      }
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        onUnauthorized()
        return
      }
      setError(err instanceof Error ? err.message : '삭제 실패')
    }
  }

  if (loading) return <p>로딩 중…</p>
  if (error && !board && !isNew) return <p className="text-danger">{error}</p>

  return (
    <div className="box box-primary">
      <div className="box-header with-border">
        <h3 className="box-title">{isNew ? '대시보드 등록' : '대시보드 편집'}</h3>
        <div className="box-tools pull-right">
          <Link to="/boards" className="btn btn-default btn-sm">
            목록
          </Link>
        </div>
      </div>
      <form className="box-body" onSubmit={handleSave}>
        <FormAlerts message={message} error={error} />
        <div className="form-group">
          <label htmlFor="board-name">이름</label>
          <input
            id="board-name"
            className="form-control"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="board-category">카테고리</label>
          <select
            id="board-category"
            className="form-control"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
          >
            <option value="">(없음)</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label htmlFor="board-layout">layoutJson</label>
          <textarea
            id="board-layout"
            className="form-control"
            rows={12}
            value={layoutJson}
            onChange={(e) => setLayoutJson(e.target.value)}
            spellCheck={false}
          />
        </div>
        <button type="submit" className="btn btn-primary">
          저장
        </button>
        {!isNew && (
          <button type="button" className="btn btn-danger pull-right" onClick={() => void handleDelete()}>
            삭제
          </button>
        )}
      </form>
    </div>
  )
}
