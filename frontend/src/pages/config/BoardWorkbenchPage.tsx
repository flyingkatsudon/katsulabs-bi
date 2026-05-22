import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api, ApiError } from '../../api/client'
import type {
  BoardDetail,
  BoardSummary,
  CategoryItem,
  ServiceResult,
  WidgetSummary,
} from '../../api/types'
import { FreeLayoutEditor } from '../../components/board/FreeLayoutEditor'
import { ConfigJsTree } from '../../components/config/ConfigJsTree'
import { ConfigEditorPane, type ConfigLoadIssue } from '../../components/config/ConfigEditorPane'
import { FormAlerts } from '../../components/FormAlerts'
import { useAuthOutletContext } from '../../hooks/useAuthOutletContext'
import { canPublishBoard } from '../../utils/permissions'
import { parseConfigResourceId } from '../../utils/parseConfigResourceId'
import { resolveConfigLoadError } from '../../utils/configResourceLoad'
import { buildCategoryTreeData } from '../../utils/configTreeData'
import {
  emptyFreeLayout,
  emptyGridLayout,
  emptyTimelineLayout,
  isFreeLayout,
  parseBoardLayout,
  serializeBoardLayout,
  type BoardLayout,
  type BoardParam,
  type BoardRow,
  type BoardWidgetSlot,
} from '../../utils/boardModel'

type BoardWorkbenchPageProps = {
  onSessionExpired: () => void
  boards: BoardSummary[]
  onBoardsChange: () => void
}

export function BoardWorkbenchPage({ onSessionExpired, boards, onBoardsChange }: BoardWorkbenchPageProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedId = searchParams.get('id')
  const newType = searchParams.get('type')

  const [categories, setCategories] = useState<CategoryItem[]>([])
  const [widgets, setWidgets] = useState<WidgetSummary[]>([])
  const [name, setName] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const [layout, setLayout] = useState<BoardLayout>(emptyGridLayout())
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [showNewMenu, setShowNewMenu] = useState(false)
  const [loadIssue, setLoadIssue] = useState<ConfigLoadIssue>(null)
  const [publishedToViewers, setPublishedToViewers] = useState(false)
  const { roleId } = useAuthOutletContext()
  const showPublish = canPublishBoard(roleId)

  const resource = useMemo(() => parseConfigResourceId(selectedId), [selectedId])
  const isNew = resource.kind === 'new'
  const numericId = resource.kind === 'edit' ? resource.id : null
  const editorOpen = resource.kind === 'new' || resource.kind === 'edit'

  const treeData = useMemo(
    () =>
      buildCategoryTreeData(
        boards.map((b) => ({
          id: b.id,
          name: b.name,
          categoryName: b.categoryName ?? 'Demo',
        })),
      ),
    [boards],
  )

  const loadMeta = useCallback(async () => {
    const [cats, wgts] = await Promise.all([
      api.get<CategoryItem[]>('/api/v1/categories'),
      api.get<WidgetSummary[]>('/api/v1/widgets'),
    ])
    setCategories(cats)
    setWidgets(wgts)
    if (cats[0] && !categoryId) setCategoryId(String(cats[0].id))
  }, [categoryId])

  const loadDetail = useCallback(async (id: number) => {
    const detail = await api.get<BoardDetail>(`/api/v1/boards/${id}`)
    setName(detail.name)
    setCategoryId(detail.categoryId != null ? String(detail.categoryId) : '')
    setLayout(parseBoardLayout(detail.layoutJson))
    setPublishedToViewers(Boolean(detail.publishedToViewers))
  }, [])

  useEffect(() => {
    void loadMeta()
  }, [loadMeta])

  useEffect(() => {
    if (resource.kind === 'invalid') {
      setLoadIssue('invalid_id')
      setLoading(false)
      return
    }
    setLoadIssue(null)
    if (resource.kind === 'none') {
      setLoading(false)
      return
    }
    if (isNew) {
      setName('new_board')
      setLayout(
        newType === 'timeline'
          ? emptyTimelineLayout()
          : newType === 'free'
            ? emptyFreeLayout()
            : emptyGridLayout(),
      )
      setShowNewMenu(false)
      setPublishedToViewers(false)
      setLoading(false)
      return
    }
    if (resource.kind === 'edit') {
      setLoading(true)
      setError(null)
      void loadDetail(resource.id)
        .catch((e) => {
          const resolved = resolveConfigLoadError(e, onSessionExpired)
          if (resolved === 'not_found' || resolved === 'invalid_id') {
            setLoadIssue(resolved)
            setError(null)
          } else if (resolved === 'error') {
            setError(e instanceof Error ? e.message : '로드 실패')
          }
        })
        .finally(() => setLoading(false))
    }
  }, [resource.kind, numericId, isNew, newType, loadDetail, onSessionExpired])

  const handleSelectLeaf = useCallback(
    (id: number) => {
      setMessage(null)
      setError(null)
      setLoadIssue(null)
      setSearchParams({ id: String(id) })
    },
    [setSearchParams],
  )

  function selectItem(id: string | null, type?: string) {
    setMessage(null)
    setError(null)
    setLoadIssue(null)
    if (id) {
      const params: Record<string, string> = { id }
      if (type) params.type = type
      setSearchParams(params)
    } else setSearchParams({})
  }

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    if (!name.trim()) {
      setError('이름을 입력하세요.')
      return
    }
    const body = {
      name: name.trim(),
      categoryId: categoryId ? Number(categoryId) : null,
      layoutJson: serializeBoardLayout(layout),
      publishedToViewers: showPublish ? publishedToViewers : false,
    }
    try {
      const result =
        isNew || numericId == null
          ? await api.post<ServiceResult>('/api/v1/boards', body)
          : await api.put<ServiceResult>(`/api/v1/boards/${numericId}`, body)
      if (result.status === '1') {
        setMessage('저장되었습니다.')
        onBoardsChange()
        if (result.id != null) selectItem(String(result.id))
      } else setError(result.message)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(id: number) {
    if (!window.confirm('삭제할까요?')) return
    const result = await api.delete<ServiceResult>(`/api/v1/boards/${id}`)
    if (result.status === '1') {
      onBoardsChange()
      selectItem(null)
    } else setError(result.message)
  }

  async function handleCopy() {
    if (numericId == null) return
    const detail = await api.get<BoardDetail>(`/api/v1/boards/${numericId}`)
    const body = {
      name: `${detail.name}_copy`,
      categoryId: detail.categoryId,
      layoutJson: detail.layoutJson ?? serializeBoardLayout(layout),
      publishedToViewers: false,
    }
    const result = await api.post<ServiceResult>('/api/v1/boards', body)
    if (result.status === '1' && result.id != null) {
      onBoardsChange()
      selectItem(String(result.id))
    }
  }

  function addRow() {
    setLayout((l) => ({ ...l, rows: [...(l.rows ?? []), { type: 'widget', widgets: [] }] }))
  }

  function addParamRow() {
    const defaultParam: BoardParam = {
      col: 'the_year',
      label: '연도',
      values: ['2016', '2017'],
      defaultValue: '2016',
    }
    setLayout((l) => ({
      ...l,
      rows: [{ type: 'param', params: [defaultParam] }, ...(l.rows ?? [])],
    }))
  }

  function addWidgetToRow(rowIndex: number) {
    const w = widgets[0]
    if (!w) return
    const slot: BoardWidgetSlot = {
      widgetId: w.id,
      name: w.name,
      width: 12,
    }
    setLayout((l) => ({
      ...l,
      rows: (l.rows ?? []).map((row, i) =>
        i === rowIndex ? { ...row, widgets: [...(row.widgets ?? []), slot] } : row,
      ),
    }))
  }

  const toolbarIcon = (icon: string, title: string, onClick: () => void, disabled = false) => (
    <i
      className={`fa ${icon} toolbar-icon`}
      style={{ cursor: disabled ? 'not-allowed' : 'pointer', opacity: disabled ? 0.4 : 1 }}
      title={title}
      onClick={() => {
        if (!disabled) onClick()
      }}
    />
  )

  return (
    <div id="inner-container" className="content">
      <div className="row">
        <div className="col-md-3">
          <div className="box box-solid">
            <div className="box-header with-border">
              <i className="fa fa-dashboard" /> <h3 className="box-title"> Dashboard</h3>
              <div className="box-tools pull-right operateBox">
                {toolbarIcon('fa-info', 'Info', () => {
                  const b = boards.find((x) => x.id === numericId)
                  if (b) window.alert(JSON.stringify(b, null, 2))
                }, !numericId)}
                &nbsp;&nbsp;
                {toolbarIcon('fa-copy', 'Copy', () => void handleCopy(), !numericId)}
                &nbsp;&nbsp;
                {toolbarIcon('fa-edit', 'Edit', () => {
                  if (numericId != null) void loadDetail(numericId)
                }, !numericId)}
                &nbsp;&nbsp;
                {toolbarIcon('fa-trash-o', 'Delete', () => {
                  if (numericId != null) void handleDelete(numericId)
                }, !numericId)}
                &nbsp;&nbsp;
                <i
                  className="fa fa-plus toolbar-icon"
                  style={{ cursor: 'pointer' }}
                  title="New"
                  onClick={() => setShowNewMenu((v) => !v)}
                />
                {showNewMenu && (
                  <div className="newBoard" style={{ position: 'absolute', right: 8, top: 32, zIndex: 10, background: '#fff', border: '1px solid #ddd', padding: 8 }}>
                    <div>
                      <button type="button" className="btn btn-link btn-sm" onClick={() => selectItem('new', 'grid')}>
                        New grid layout
                      </button>
                    </div>
                    <div>
                      <button type="button" className="btn btn-link btn-sm" onClick={() => selectItem('new', 'timeline')}>
                        New timeline layout
                      </button>
                    </div>
                    <div>
                      <button type="button" className="btn btn-link btn-sm" onClick={() => selectItem('new', 'free')}>
                        New free layout
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
            <ConfigJsTree
              treeId="boardTreeID"
              treeData={treeData}
              selectedId={numericId != null ? String(numericId) : null}
              onSelectLeaf={handleSelectLeaf}
              maxHeight="70vh"
            />
          </div>
        </div>
        <div className="col-md-9">
          <ConfigEditorPane
            loading={loading}
            resource={resource}
            loadIssue={loadIssue}
            resourceLabel="대시보드"
            listPath="/boards"
            idleHint="트리에서 보드를 선택하거나 + 로 새 보드를 만듭니다."
            onBackToList={() => selectItem(null)}
          >
            <div className="box">
              <div className="box-header with-border">
                <h3 className="box-title">{name}</h3>
                <div className="box-tools pull-right">
                  {isNew ? <span className="label label-danger">NEW</span> : <span className="label label-info">EDIT</span>}
                </div>
              </div>
              <div className="box-body">
                <FormAlerts message={message} error={error} />
                <form onSubmit={handleSave}>
                  <div className="form-group">
                    <button type="button" className="btn btn-danger pull-right" onClick={() => selectItem(null)}>
                      Cancel
                    </button>
                    {!isNew && numericId != null && (
                      <Link
                        to={`/mine/${numericId}`}
                        className="btn btn-primary pull-right"
                        style={{ marginRight: 5 }}
                      >
                        <i className="fa fa-eye" /> 보기
                      </Link>
                    )}
                    <button type="submit" className="btn btn-success pull-right" style={{ marginRight: 5 }}>
                      Save
                    </button>
                  </div>
                  <div className="form-group">
                    <label>Category</label>
                    <select
                      className="form-control"
                      value={categoryId}
                      onChange={(e) => setCategoryId(e.target.value)}
                    >
                      {categories.map((c) => (
                        <option key={c.id} value={c.id}>
                          {c.name}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Name</label>
                    <input id="BoardName" className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
                  </div>
                  {showPublish && (
                    <div className="form-group">
                      <label className="checkbox-inline">
                        <input
                          type="checkbox"
                          checked={publishedToViewers}
                          onChange={(e) => setPublishedToViewers(e.target.checked)}
                        />{' '}
                        Viewer에게 게시 (published)
                      </label>
                      <p className="help-block text-muted" style={{ marginTop: 4 }}>
                        체크하면 Viewer 역할 사용자가 사이드바·홈에서 이 보드를 볼 수 있습니다.
                      </p>
                    </div>
                  )}
                  {isFreeLayout(layout) ? (
                    <FreeLayoutEditor
                      widgets={layout.widgets ?? []}
                      catalog={widgets}
                      onChange={(freeWidgets) => setLayout({ type: 'free', widgets: freeWidgets })}
                    />
                  ) : (
                    <>
                  <div className="form-group" style={{ margin: '5px 15px' }}>
                    <button type="button" className="btn btn-success" onClick={addRow}>
                      Add Row
                    </button>
                    <button type="button" className="btn btn-success" style={{ marginLeft: 8 }} onClick={addParamRow}>
                      Add Param Row
                    </button>
                  </div>
                  {(layout.rows ?? []).map((row, rowIndex) => (
                    <BoardRowEditor
                      key={rowIndex}
                      row={row}
                      rowIndex={rowIndex}
                      widgets={widgets}
                      onAddWidget={() => addWidgetToRow(rowIndex)}
                      onRemoveRow={() =>
                        setLayout((l) => ({ ...l, rows: (l.rows ?? []).filter((_, i) => i !== rowIndex) }))
                      }
                      onUpdateRow={(updated) =>
                        setLayout((l) => ({
                          ...l,
                          rows: (l.rows ?? []).map((r, i) => (i === rowIndex ? updated : r)),
                        }))
                      }
                    />
                  ))}
                    </>
                  )}
                </form>
              </div>
            </div>
          </ConfigEditorPane>
        </div>
      </div>
    </div>
  )
}

function BoardRowEditor({
  row,
  rowIndex,
  widgets,
  onAddWidget,
  onRemoveRow,
  onUpdateRow,
}: {
  row: BoardRow
  rowIndex: number
  widgets: WidgetSummary[]
  onAddWidget: () => void
  onRemoveRow: () => void
  onUpdateRow: (row: BoardRow) => void
}) {
  if (row.type === 'param') {
    const params = row.params ?? []

    function updateParams(next: BoardParam[]) {
      onUpdateRow({ ...row, params: next })
    }

    return (
      <div className="box box-warning" style={{ margin: '8px 15px' }}>
        <div className="box-header">
          Param Row #{rowIndex + 1}
          <div className="box-tools pull-right">
            <button
              type="button"
              className="btn btn-xs btn-warning"
              onClick={() =>
                updateParams([
                  ...params,
                  { col: '', label: '', values: [], defaultValue: '' },
                ])
              }
            >
              <i className="fa fa-plus" /> 파라미터
            </button>
            <button type="button" className="btn btn-box-tool" onClick={onRemoveRow}>
              <i className="fa fa-times" />
            </button>
          </div>
        </div>
        <div className="box-body">
          {params.length === 0 && (
            <p className="text-muted">보드 필터 파라미터가 없습니다. 위 버튼으로 추가하세요.</p>
          )}
          {params.map((p, pi) => (
            <div key={pi} className="row" style={{ marginBottom: 8 }}>
              <div className="col-md-3">
                <label className="small text-muted">컬럼 (col)</label>
                <input
                  className="form-control input-sm"
                  placeholder="the_year"
                  value={p.col}
                  onChange={(e) => {
                    const next = [...params]
                    next[pi] = { ...p, col: e.target.value }
                    updateParams(next)
                  }}
                />
              </div>
              <div className="col-md-2">
                <label className="small text-muted">라벨</label>
                <input
                  className="form-control input-sm"
                  placeholder="연도"
                  value={p.label ?? ''}
                  onChange={(e) => {
                    const next = [...params]
                    next[pi] = { ...p, label: e.target.value }
                    updateParams(next)
                  }}
                />
              </div>
              <div className="col-md-4">
                <label className="small text-muted">선택값 (쉼표 구분)</label>
                <input
                  className="form-control input-sm"
                  placeholder="2016,2017"
                  value={(p.values ?? []).join(',')}
                  onChange={(e) => {
                    const next = [...params]
                    next[pi] = {
                      ...p,
                      values: e.target.value
                        .split(',')
                        .map((s) => s.trim())
                        .filter(Boolean),
                    }
                    updateParams(next)
                  }}
                />
              </div>
              <div className="col-md-2">
                <label className="small text-muted">기본값</label>
                <input
                  className="form-control input-sm"
                  value={p.defaultValue ?? ''}
                  onChange={(e) => {
                    const next = [...params]
                    next[pi] = { ...p, defaultValue: e.target.value }
                    updateParams(next)
                  }}
                />
              </div>
              <div className="col-md-1" style={{ paddingTop: 22 }}>
                <button
                  type="button"
                  className="btn btn-box-tool"
                  title="삭제"
                  onClick={() => {
                    const next = [...params]
                    next.splice(pi, 1)
                    updateParams(next)
                  }}
                >
                  <i className="fa fa-times" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="box box-success" style={{ margin: '8px 15px' }}>
      <div className="box-header">
        Row #{rowIndex + 1}
        <div className="box-tools pull-right">
          <input
            type="text"
            className="form-control input-sm"
            style={{ width: 120, display: 'inline-block' }}
            placeholder="Height"
            value={row.height ?? ''}
            onChange={(e) => onUpdateRow({ ...row, height: e.target.value })}
          />
          <button type="button" className="btn btn-xs btn-success" style={{ marginLeft: 8 }} onClick={onAddWidget}>
            Add Column
          </button>
          <button type="button" className="btn btn-box-tool" onClick={onRemoveRow}>
            <i className="fa fa-times" />
          </button>
        </div>
      </div>
      <div className="box-body">
        <div className="row">
          {(row.widgets ?? []).map((slot, wi) => (
            <div key={wi} className={`col-md-${slot.width}`}>
              <div className="box box-primary">
                <div className="box-header">
                  <select
                    className="form-control input-sm"
                    value={slot.widgetId}
                    onChange={(e) => {
                      const w = widgets.find((x) => x.id === Number(e.target.value))
                      const widgetsSlots = [...(row.widgets ?? [])]
                      widgetsSlots[wi] = {
                        ...slot,
                        widgetId: Number(e.target.value),
                        name: w?.name ?? slot.name,
                      }
                      onUpdateRow({ ...row, widgets: widgetsSlots })
                    }}
                  >
                    {widgets.map((w) => (
                      <option key={w.id} value={w.id}>
                        {w.name}
                      </option>
                    ))}
                  </select>
                  <input
                    type="number"
                    min={1}
                    max={12}
                    className="form-control input-sm"
                    style={{ width: 60, display: 'inline-block', marginTop: 4 }}
                    value={slot.width}
                    onChange={(e) => {
                      const widgetsSlots = [...(row.widgets ?? [])]
                      widgetsSlots[wi] = { ...slot, width: Number(e.target.value) || 12 }
                      onUpdateRow({ ...row, widgets: widgetsSlots })
                    }}
                  />
                  <button
                    type="button"
                    className="btn btn-box-tool pull-right"
                    onClick={() => {
                      const widgetsSlots = [...(row.widgets ?? [])]
                      widgetsSlots.splice(wi, 1)
                      onUpdateRow({ ...row, widgets: widgetsSlots })
                    }}
                  >
                    <i className="fa fa-times" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
