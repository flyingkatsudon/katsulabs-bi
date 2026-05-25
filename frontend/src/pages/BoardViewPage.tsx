import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useOutletContext, useParams } from 'react-router-dom'
import { api, ApiError } from '../api/client'
import type { AuthOutletContext } from '../auth/AuthOutletContext'
import type { BoardDetail } from '../api/types'
import { DashboardWidgetBox } from '../components/board/DashboardWidgetBox'
import { FormAlerts } from '../components/FormAlerts'
import type { ExtraAggregateFilter } from '../utils/aggregateApi'
import {
  FREE_LAYOUT_GRID,
  isFreeLayout,
  parseBoardLayout,
  type BoardParam,
  type BoardRow,
} from '../utils/boardModel'

type BoardViewPageProps = {
  onUnauthorized: () => void
}

function rowHeight(row: BoardRow): number {
  const h = row.height ? Number.parseInt(String(row.height), 10) : 300
  return Number.isFinite(h) ? h : 300
}

export function BoardViewPage({ onUnauthorized }: BoardViewPageProps) {
  const { canWriteDashboard, bootstrapped } = useOutletContext<AuthOutletContext>()
  const { id } = useParams()
  const boardId = id ? Number(id) : NaN
  const [board, setBoard] = useState<BoardDetail | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [paramValues, setParamValues] = useState<Record<string, string>>({})
  const [syncAllToken, setSyncAllToken] = useState(0)

  const load = useCallback(async () => {
    if (!Number.isFinite(boardId)) {
      setError('잘못된 보드 ID 입니다.')
      setLoading(false)
      return
    }
    setLoading(true)
    setError(null)
    try {
      setBoard(await api.get<BoardDetail>(`/api/v1/boards/${boardId}`))
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        onUnauthorized()
        return
      }
      setError(e instanceof Error ? e.message : '보드 로드 실패')
      setBoard(null)
    } finally {
      setLoading(false)
    }
  }, [boardId, onUnauthorized])

  useEffect(() => {
    if (!bootstrapped) return
    void load()
  }, [load, bootstrapped])

  useEffect(() => {
    setSyncAllToken(0)
  }, [boardId])

  const layout = board ? parseBoardLayout(board.layoutJson) : null
  const freeMode = layout ? isFreeLayout(layout) : false
  const paramRows = useMemo(
    () => layout?.rows?.filter((r) => r.type === 'param') ?? [],
    [layout],
  )
  const allParams = useMemo(
    () => paramRows.flatMap((r) => (r.params ?? []) as BoardParam[]),
    [paramRows],
  )
  const widgetRows = useMemo(
    () => layout?.rows?.filter((r) => r.type !== 'param' && (r.widgets?.length ?? 0) > 0) ?? [],
    [layout],
  )
  const freeWidgets = useMemo(() => layout?.widgets ?? [], [layout])
  const hasWidgets = freeMode ? freeWidgets.length > 0 : widgetRows.length > 0

  const boardFilters: ExtraAggregateFilter[] = useMemo(() => {
    return allParams
      .map((p) => {
        const col = p.col?.trim()
        const val = paramValues[col ?? '']
        if (!col || !val) return null
        return { col, values: [val] }
      })
      .filter((x): x is ExtraAggregateFilter => x != null)
  }, [allParams, paramValues])

  const [paramsReady, setParamsReady] = useState(false)

  useEffect(() => {
    if (allParams.length === 0) {
      setParamsReady(true)
      return
    }
    setParamValues((prev) => {
      let changed = false
      const next = { ...prev }
      for (const p of allParams) {
        const col = p.col?.trim()
        if (!col || next[col]) continue
        next[col] = p.defaultValue ?? p.values?.[0] ?? ''
        changed = true
      }
      return changed ? next : prev
    })
    setParamsReady(true)
  }, [allParams])

  return (
    <>
      <section className="content-header">
        <h1>
          {board?.name ?? 'Dashboard'}
          <small>{board?.categoryName ?? ''}</small>
          {!loading && hasWidgets && (
            <button
              type="button"
              className="btn btn-default btn-sm"
              style={{ marginLeft: 12, verticalAlign: 'middle' }}
              title="이 보드의 모든 차트 데이터 새로고침"
              onClick={() => setSyncAllToken((t) => t + 1)}
            >
              <span className="fa fa-refresh" aria-hidden /> 전체 새로고침
            </button>
          )}
        </h1>
        <ol className="breadcrumb">
          <li>
            <Link to="/">
              <i className="fa fa-dashboard" aria-hidden /> Dashboard
            </Link>
          </li>
          {board?.categoryName ? <li>{board.categoryName}</li> : null}
          <li className="active">{board?.name ?? 'View'}</li>
        </ol>
      </section>
      <section className="content">
        <FormAlerts message={null} error={error} />
        {loading && (
          <p className="text-muted">
            <span className="fa fa-refresh fa-spin" aria-hidden /> 로딩 중…
          </p>
        )}
        {!loading && board && paramsReady && allParams.length > 0 && (
          <div className="box box-warning">
            <div className="box-header with-border">
              <h3 className="box-title">필터</h3>
            </div>
            <div className="box-body">
              <p className="text-muted" style={{ marginTop: 0 }}>
                필터를 바꾼 뒤 상단 <strong>전체 새로고침</strong> 또는 각 위젯의{' '}
                <span className="fa fa-refresh" aria-hidden /> 버튼으로 반영하세요.
              </p>
              <div className="row">
                {allParams.map((p) => {
                  const col = p.col?.trim() ?? ''
                  if (!col) return null
                  const label = p.label ?? col
                  const options = p.values ?? []
                  return (
                    <div key={col} className="col-md-3 form-group">
                      <label>{label}</label>
                      {options.length > 0 ? (
                        <select
                          className="form-control"
                          value={paramValues[col] ?? ''}
                          onChange={(e) =>
                            setParamValues((v) => ({ ...v, [col]: e.target.value }))
                          }
                        >
                          <option value="">(전체)</option>
                          {options.map((o) => (
                            <option key={o} value={o}>
                              {o}
                            </option>
                          ))}
                        </select>
                      ) : (
                        <input
                          className="form-control"
                          value={paramValues[col] ?? ''}
                          onChange={(e) =>
                            setParamValues((v) => ({ ...v, [col]: e.target.value }))
                          }
                        />
                      )}
                    </div>
                  )
                })}
              </div>
            </div>
          </div>
        )}
        {!loading && board && paramsReady && !hasWidgets && (
          <div className="callout callout-info">
            <p>이 보드에 배치된 위젯이 없습니다.</p>
            {canWriteDashboard && (
              <p>
                <Link to={`/boards?id=${board.id}`} className="btn btn-primary btn-sm">
                  보드 편집
                </Link>
              </p>
            )}
          </div>
        )}
        {paramsReady && freeMode && (
          <div style={{ position: 'relative', minHeight: '70vh' }}>
            {freeWidgets.map((w) => (
              <div
                key={w.widgetId}
                style={{
                  position: 'absolute',
                  left: `${(w.x / FREE_LAYOUT_GRID) * 100}%`,
                  top: `${(w.y / FREE_LAYOUT_GRID) * 100}%`,
                  width: `${((w.ex - w.x) / FREE_LAYOUT_GRID) * 100}%`,
                  height: `${((w.ey - w.y) / FREE_LAYOUT_GRID) * 100}%`,
                  minHeight: 160,
                  padding: 4,
                  boxSizing: 'border-box',
                }}
              >
                <DashboardWidgetBox
                  widgetId={w.widgetId}
                  name={w.name}
                  height={280}
                  boardFilters={boardFilters}
                  canEditWidget={canWriteDashboard}
                  syncAllToken={syncAllToken}
                />
              </div>
            ))}
          </div>
        )}
        {paramsReady &&
          !freeMode &&
          widgetRows.map((row, ri) => (
            <div key={ri} className="row" style={{ marginBottom: 8 }}>
              {(row.widgets ?? []).map((slot) => (
                <div key={slot.widgetId} className={`col-md-${slot.width || 12}`}>
                  <DashboardWidgetBox
                    widgetId={slot.widgetId}
                    name={slot.name}
                    height={rowHeight(row)}
                    boardFilters={boardFilters}
                    canEditWidget={canWriteDashboard}
                    syncAllToken={syncAllToken}
                  />
                </div>
              ))}
            </div>
          ))}
      </section>
    </>
  )
}
