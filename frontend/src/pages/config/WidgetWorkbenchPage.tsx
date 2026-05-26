import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api, ApiError } from '../../api/client'
import type { DatasetDetail, DatasetSummary, ServiceResult, WidgetDetail, WidgetSummary } from '../../api/types'
import { ConfigJsTree } from '../../components/config/ConfigJsTree'
import { ConfigEditorPane, type ConfigLoadIssue } from '../../components/config/ConfigEditorPane'
import { FormAlerts } from '../../components/FormAlerts'
import { parseConfigResourceId } from '../../utils/parseConfigResourceId'
import { resolveConfigLoadError } from '../../utils/configResourceLoad'
import { WidgetChartView } from '../../components/widget/WidgetChartView'
import { WidgetSchemaPanel } from '../../components/widget/WidgetSchemaPanel'
import { ChartConfigGuide } from '../../components/widget/ChartConfigGuide'
import { WidgetChartConfig } from '../../components/widget/WidgetChartConfig'
import { getChartGuide, validateWidgetForAggregate } from '../../utils/chartGuide'
import { applyWidgetMappingReset } from '../../utils/widgetStarter'
import { parseDatasetData, type DatasetData } from '../../utils/datasetModel'
import { buildCategoryTreeData, filterTreeList } from '../../utils/configTreeData'
import {
  fetchAggregate,
  fetchAggregateViewQuery,
  type AggregateResult,
} from '../../utils/aggregateApi'
import { chartConfigZones } from '../../utils/chartRender'
import {
  CHART_TYPES,
  defaultWidgetData,
  formatWidgetDisplayName,
  parseWidgetData,
  serializeWidgetData,
  splitWidgetDisplayName,
  toWidgetCol,
  type WidgetDataModel,
} from '../../utils/widgetModel'

type WidgetWorkbenchPageProps = {
  onSessionExpired: () => void
}

export function WidgetWorkbenchPage({ onSessionExpired }: WidgetWorkbenchPageProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedId = searchParams.get('id')
  const [keywords, setKeywords] = useState('')
  const [liteMode, setLiteMode] = useState(false)

  const [list, setList] = useState<WidgetSummary[]>([])
  const [datasets, setDatasets] = useState<DatasetSummary[]>([])
  const [widgetDisplayName, setWidgetDisplayName] = useState('Default Category/new_widget')
  const [model, setModel] = useState<WidgetDataModel>(() => defaultWidgetData(1, 'table'))
  const [datasetSchema, setDatasetSchema] = useState<DatasetData | null>(null)
  const [schemaDatasetId, setSchemaDatasetId] = useState<number | null>(null)
  const [tab, setTab] = useState<'preview' | 'query' | 'option'>('preview')
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [previewState, setPreviewState] = useState<{
    result: AggregateResult
    config: WidgetDataModel['config']
  } | null>(null)
  const [querySql, setQuerySql] = useState<string | null>(null)
  const [previewBusy, setPreviewBusy] = useState(false)
  const [schemaPickTarget, setSchemaPickTarget] = useState<'row' | 'column' | 'value'>('row')
  const lastDatasetIdRef = useRef<number | null>(null)
  const autoPreviewOnLoadRef = useRef(false)

  function invalidatePreview() {
    setPreviewState(null)
    setQuerySql(null)
  }

  const [loadIssue, setLoadIssue] = useState<ConfigLoadIssue>(null)
  const resource = useMemo(() => parseConfigResourceId(selectedId), [selectedId])
  const isNew = resource.kind === 'new'
  const numericId = resource.kind === 'edit' ? resource.id : null

  const treeData = useMemo(
    () =>
      buildCategoryTreeData(
        filterTreeList(
          list.map((w) => ({
            id: w.id,
            name: w.name,
            categoryName: w.categoryName ?? 'Default Category',
          })),
          keywords,
        ),
      ),
    [list, keywords],
  )

  const loadList = useCallback(async () => {
    const [widgets, ds] = await Promise.all([
      api.get<WidgetSummary[]>('/api/v1/widgets'),
      api.get<DatasetSummary[]>('/api/v1/datasets'),
    ])
    setList(widgets)
    setDatasets(ds)
  }, [])

  const loadDatasetSchema = useCallback(async (datasetId: number) => {
    const detail = await api.get<DatasetDetail>(`/api/v1/datasets/${datasetId}`)
    setDatasetSchema(parseDatasetData(detail.dataJson))
    setSchemaDatasetId(datasetId)
  }, [])

  const loadDetail = useCallback(
    async (id: number) => {
      const detail = await api.get<WidgetDetail>(`/api/v1/widgets/${id}`)
      setWidgetDisplayName(formatWidgetDisplayName(detail.categoryName, detail.name))
      const parsed = parseWidgetData(detail.dataJson)
      lastDatasetIdRef.current = parsed.datasetId ?? null
      setPreviewState(null)
      setQuerySql(null)
      setModel(parsed)
      if (parsed.datasetId) await loadDatasetSchema(parsed.datasetId)
    },
    [loadDatasetSchema],
  )

  useEffect(() => {
    void (async () => {
      if (resource.kind === 'invalid') {
        setLoadIssue('invalid_id')
        setLoading(false)
        return
      }
      setLoadIssue(null)
      setLoading(true)
      setError(null)
      try {
        await loadList()
        if (isNew) {
          setWidgetDisplayName('Default Category/new_widget')
          lastDatasetIdRef.current = null
          setPreviewState(null)
          setQuerySql(null)
          setModel(defaultWidgetData(1, 'line'))
          await loadDatasetSchema(1)
          autoPreviewOnLoadRef.current = true
        } else if (resource.kind === 'edit') {
          await loadDetail(resource.id)
          autoPreviewOnLoadRef.current = true
        }
      } catch (e) {
        const resolved = resolveConfigLoadError(e, onSessionExpired)
        if (resolved === 'not_found' || resolved === 'invalid_id') {
          setLoadIssue(resolved)
          setError(null)
        } else if (resolved === 'error') {
          setError(e instanceof Error ? e.message : '로드 실패')
        }
      } finally {
        setLoading(false)
      }
    })()
  }, [resource.kind, numericId, isNew, loadDetail, loadList, loadDatasetSchema, onSessionExpired])

  useEffect(() => {
    if (model.datasetId) void loadDatasetSchema(model.datasetId)
  }, [model.datasetId, loadDatasetSchema])

  useEffect(() => {
    if (!datasetSchema || !model.datasetId) return
    if (schemaDatasetId !== model.datasetId) return
    if (lastDatasetIdRef.current === model.datasetId) return
    lastDatasetIdRef.current = model.datasetId
    setModel((m) => applyWidgetMappingReset(m, datasetSchema))
    invalidatePreview()
  }, [model.datasetId, datasetSchema, schemaDatasetId])

  function selectItem(id: string | null) {
    setMessage(null)
    setError(null)
    setLoadIssue(null)
    if (id) setSearchParams({ id })
    else setSearchParams({})
  }

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    const { categoryName, name } = splitWidgetDisplayName(widgetDisplayName)
    if (!name) {
      setError('위젯 이름을 입력하세요.')
      return
    }
    const body = { name, categoryName, dataJson: serializeWidgetData(model) }
    try {
      const result =
        isNew || numericId == null
          ? await api.post<ServiceResult>('/api/v1/widgets', body)
          : await api.put<ServiceResult>(`/api/v1/widgets/${numericId}`, body)
      if (result.status === '1') {
        setMessage('저장되었습니다.')
        await loadList()
        if (result.id != null) selectItem(String(result.id))
      } else setError(result.message)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(id: number) {
    if (!window.confirm('삭제할까요?')) return
    const result = await api.delete<ServiceResult>(`/api/v1/widgets/${id}`)
    if (result.status === '1') {
      await loadList()
      selectItem(null)
    } else setError(result.message)
  }

  async function handleCopy() {
    if (numericId == null) return
    const detail = await api.get<WidgetDetail>(`/api/v1/widgets/${numericId}`)
    const src = list.find((w) => w.id === numericId)
    const body = {
      name: `${src?.name ?? 'widget'}_copy`,
      categoryName: src?.categoryName ?? 'Default Category',
      dataJson: detail.dataJson ?? serializeWidgetData(model),
    }
    const result = await api.post<ServiceResult>('/api/v1/widgets', body)
    if (result.status === '1' && result.id != null) {
      await loadList()
      selectItem(String(result.id))
    }
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

  const leftCol = liteMode ? 'col-md-2' : 'col-md-3'
  const rightCol = liteMode ? 'col-md-10' : 'col-md-9'

  const zones = chartConfigZones(model.config.chart_type ?? 'table')

  useEffect(() => {
    if (!zones.row && zones.value) setSchemaPickTarget('value')
    else if (!zones.row && zones.column) setSchemaPickTarget('column')
    else setSchemaPickTarget('row')
  }, [model.config.chart_type, zones.row, zones.column, zones.value])

  function addDimension(col: string, alias?: string) {
    const wcol = toWidgetCol({ column: col, alias, type: 'column' })
    setModel((m) => ({
      ...m,
      config: { ...m.config, keys: [...(m.config.keys ?? []), wcol] },
    }))
    invalidatePreview()
  }

  function addGroup(col: string, alias?: string) {
    const wcol = toWidgetCol({ column: col, alias, type: 'column' })
    setModel((m) => ({
      ...m,
      config: { ...m.config, groups: [...(m.config.groups ?? []), wcol] },
    }))
    invalidatePreview()
  }

  function addMeasure(col: string, alias?: string) {
    const wcol = toWidgetCol({ column: col, alias, type: 'column' }, true)
    setModel((m) => {
      const values = m.config.values?.length ? [...m.config.values] : [{ cols: [] }]
      values[0] = { cols: [...(values[0].cols ?? []), wcol] }
      return { ...m, config: { ...m.config, values } }
    })
    invalidatePreview()
  }

  async function runPreview(target: WidgetDataModel) {
    if (!target.datasetId) {
      setError('데이터셋을 선택하세요.')
      return
    }
    const chartType = target.config.chart_type ?? 'table'
    const validation = validateWidgetForAggregate(chartType, target.config)
    if (validation) {
      setError(validation)
      setTab('preview')
      return
    }
    setPreviewBusy(true)
    setError(null)
    setTab('preview')
    try {
      const result = await fetchAggregate(target.datasetId, target, false)
      setPreviewState({ result, config: structuredClone(target.config) })
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Preview 실패')
      invalidatePreview()
    } finally {
      setPreviewBusy(false)
    }
  }

  useEffect(() => {
    if (loading) return
    if (!autoPreviewOnLoadRef.current) return
    if (!model.datasetId) return
    if (schemaDatasetId !== model.datasetId) return
    if (lastDatasetIdRef.current !== model.datasetId) return
    autoPreviewOnLoadRef.current = false
    void runPreview(model)
  }, [loading, model, schemaDatasetId])

  async function handlePreview() {
    await runPreview(model)
  }

  async function handlePreviewQuery() {
    if (!model.datasetId) {
      setError('데이터셋을 선택하세요.')
      return
    }
    const validation = validateWidgetForAggregate(model.config.chart_type ?? 'table', model.config)
    if (validation) {
      setError(validation)
      setTab('query')
      return
    }
    setPreviewBusy(true)
    setError(null)
    setTab('query')
    try {
      setQuerySql(await fetchAggregateViewQuery(model.datasetId, model))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Preview Query 실패')
      setQuerySql(null)
    } finally {
      setPreviewBusy(false)
    }
  }

  return (
    <div id="inner-container" className="content">
      <div className="row">
        <div className={leftCol}>
          <div className="box box-solid">
            <div className="box-header with-border">
              <i className="fa fa-bar-chart-o" /> <h3 className="box-title"> Widget</h3>
              <div className="box-tools pull-right">
                {toolbarIcon('fa-info', 'Info', () => {
                  const w = list.find((x) => x.id === numericId)
                  if (w) window.alert(JSON.stringify(w, null, 2))
                }, !numericId)}
                &nbsp;
                {toolbarIcon('fa-plus', 'New', () => selectItem('new'))}
                {!liteMode && (
                  <>
                    &nbsp;
                    {toolbarIcon('fa-copy', 'Copy', () => void handleCopy(), !numericId)}
                    &nbsp;
                    {toolbarIcon('fa-edit', 'Edit', () => {
                      if (numericId != null) void loadDetail(numericId)
                    }, !numericId)}
                    &nbsp;
                    {toolbarIcon('fa-trash-o', 'Delete', () => {
                      if (numericId != null) void handleDelete(numericId)
                    }, !numericId)}
                  </>
                )}
              </div>
            </div>
            <div className="box-body">
              <input
                type="text"
                className="form-control"
                placeholder="Search"
                title="wg:test ds:Bill"
                value={keywords}
                onChange={(e) => setKeywords(e.target.value)}
              />
              <ConfigJsTree
                treeId="widgetTreeID"
                treeData={treeData}
                selectedId={numericId != null ? String(numericId) : null}
                onSelectLeaf={(id) => selectItem(String(id))}
              />
            </div>
          </div>
          {datasetSchema && (
            <div className="box box-solid">
              <div className="box-header with-border">
                <i className="fa fa-cube" /> <h3 className="box-title"> Dataset</h3>
                <div className="box-tools pull-right">
                  <i
                    className="fa fa-refresh toolbar-icon"
                    style={{ cursor: 'pointer' }}
                    title="Refresh"
                    onClick={() => model.datasetId && void loadDatasetSchema(model.datasetId)}
                  />
                </div>
              </div>
              <div className="box-body" style={{ padding: '10px 0' }}>
                <div className="btn-group btn-group-xs" style={{ margin: '0 10px 8px', display: 'flex' }}>
                  {zones.row && (
                    <button
                      type="button"
                      className={`btn btn-default ${schemaPickTarget === 'row' ? 'active' : ''}`}
                      onClick={() => setSchemaPickTarget('row')}
                    >
                      → Row
                    </button>
                  )}
                  {zones.column && (
                    <button
                      type="button"
                      className={`btn btn-default ${schemaPickTarget === 'column' ? 'active' : ''}`}
                      onClick={() => setSchemaPickTarget('column')}
                    >
                      → Column
                    </button>
                  )}
                  {zones.value && (
                    <button
                      type="button"
                      className={`btn btn-default ${schemaPickTarget === 'value' ? 'active' : ''}`}
                      onClick={() => setSchemaPickTarget('value')}
                    >
                      → Value
                    </button>
                  )}
                </div>
                <WidgetSchemaPanel
                  dataset={datasetSchema}
                  onPickDimension={(col, alias) => {
                    if (schemaPickTarget === 'value') addMeasure(col, alias)
                    else if (schemaPickTarget === 'column') addGroup(col, alias)
                    else addDimension(col, alias)
                  }}
                  onPickMeasure={addMeasure}
                />
              </div>
            </div>
          )}
        </div>
        <div className={rightCol} style={{ paddingLeft: 0 }}>
          <ConfigEditorPane
            loading={loading}
            resource={resource}
            loadIssue={loadIssue}
            resourceLabel="위젯"
            listPath="/widgets"
            idleHint="트리에서 위젯을 선택하거나 + 로 새 위젯을 만듭니다."
            onBackToList={() => selectItem(null)}
          >
            <div className="box">
              <div className="box-header with-border">
                <div className="user-block">
                  <span className="username" style={{ marginLeft: 0 }}>
                    {widgetDisplayName}
                  </span>
                  <span className="description" style={{ marginLeft: 0 }}>
                    {datasetsLabel(model.datasetId, datasetSchema)}
                  </span>
                </div>
                <div className="box-tools pull-right">
                  {isNew ? <span className="label label-danger">NEW</span> : <span className="label label-info">EDIT</span>}
                </div>
              </div>
              <div className="box-body" style={{ minHeight: '66vh' }}>
                <FormAlerts message={message} error={error} />
                <form className="form-horizontal" onSubmit={handleSave}>
                  <div className="form-group">
                    <label className="col-sm-2 control-label">Dataset</label>
                    <div className="col-sm-10">
                      <select
                        className="form-control"
                        value={model.datasetId ?? ''}
                        onChange={(e) => {
                          const datasetId = Number(e.target.value)
                          if (datasetId === model.datasetId) return
                          lastDatasetIdRef.current = null
                          invalidatePreview()
                          setModel((m) => ({ ...m, datasetId }))
                        }}
                      >
                        {datasets.map((d) => (
                          <option key={d.id} value={d.id}>
                            {d.categoryName}/{d.name}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="col-sm-2 control-label">Widget name</label>
                    <div className="col-sm-10">
                      <input
                        id="widgetName"
                        className="form-control"
                        value={widgetDisplayName}
                        onChange={(e) => setWidgetDisplayName(e.target.value)}
                      />
                    </div>
                  </div>
                  {datasetSchema && (
                    <>
                      <div className="form-group">
                        <label className="col-sm-2 control-label">
                          Widget type
                          <ChartConfigGuide chartType={model.config.chart_type ?? 'table'} compact />
                        </label>
                        <div className="col-sm-10 chart-type">
                          <ul className="list-inline">
                            {CHART_TYPES.map((chart) => {
                              const tip = getChartGuide(chart.value).summary
                              return (
                                <li key={chart.value}>
                                  <a
                                    href="#"
                                    className={model.config.chart_type === chart.value ? 'active' : ''}
                                    title={tip}
                                    onClick={(e) => {
                                      e.preventDefault()
                                      const ct = chart.value
                                      if (ct === model.config.chart_type) return
                                      setModel((m) =>
                                        datasetSchema
                                          ? applyWidgetMappingReset(m, datasetSchema, ct)
                                          : m,
                                      )
                                      invalidatePreview()
                                    }}
                                  >
                                    <i className={`chart-type-icon ${chart.class}`} />
                                  </a>
                                </li>
                              )
                            })}
                          </ul>
                        </div>
                      </div>
                      <ChartConfigGuide chartType={model.config.chart_type ?? 'table'} />
                      <WidgetChartConfig
                        model={model}
                        onChange={(next) => {
                          setModel(next)
                          invalidatePreview()
                        }}
                        onDropRow={(field) => addDimension(field.column, field.alias)}
                        onDropColumn={(field) => addGroup(field.column, field.alias)}
                        onDropValue={(field) => addMeasure(field.column, field.alias)}
                      />
                    </>
                  )}
                  <div className="nav-tabs-custom" style={{ marginTop: 12 }}>
                    <ul className="nav nav-tabs">
                      <li className={tab === 'preview' ? 'active' : undefined}>
                        <a href="#preview" onClick={(e) => { e.preventDefault(); setTab('preview') }}>
                          Preview
                        </a>
                      </li>
                      <li className={tab === 'query' ? 'active' : undefined}>
                        <a href="#query" onClick={(e) => { e.preventDefault(); setTab('query') }}>
                          Query
                        </a>
                      </li>
                      <li className={tab === 'option' ? 'active' : undefined}>
                        <a href="#option" onClick={(e) => { e.preventDefault(); setTab('option') }}>
                          Option
                        </a>
                      </li>
                      <li className="pull-right">
                        <button
                          type="button"
                          className="btn btn-success btn-sm"
                          disabled={previewBusy}
                          onClick={() => void handlePreview()}
                        >
                          Preview
                        </button>
                        <button
                          type="button"
                          className="btn btn-success btn-sm"
                          style={{ marginLeft: 4 }}
                          disabled={previewBusy}
                          onClick={() => void handlePreviewQuery()}
                        >
                          Preview Query
                        </button>
                        <button type="submit" className="btn btn-success btn-sm" style={{ marginLeft: 4 }}>
                          Save
                        </button>
                        <button
                          type="button"
                          className="btn btn-danger btn-sm"
                          style={{ marginLeft: 4 }}
                          onClick={() => selectItem(null)}
                        >
                          Cancel
                        </button>
                        <button
                          type="button"
                          className="btn btn-info btn-sm"
                          style={{ marginLeft: 4 }}
                          onClick={() => setLiteMode((v) => !v)}
                        >
                          {liteMode ? 'S' : 'L'}
                        </button>
                      </li>
                    </ul>
                    <div className="tab-content" style={{ minHeight: '43vh' }}>
                      {tab === 'preview' && (
                        <div className="tab-pane active" id="preview_widget" style={{ minHeight: 300 }}>
                          {previewState ? (
                            <WidgetChartView
                              chartType={previewState.config.chart_type ?? 'table'}
                              result={previewState.result}
                              widgetConfig={previewState.config}
                              widgetId={numericId ?? undefined}
                              height={300}
                            />
                          ) : (
                            <p className="text-muted">Preview 를 눌러 집계 결과를 확인하세요.</p>
                          )}
                        </div>
                      )}
                      {tab === 'query' && (
                        <div className="tab-pane active">
                          <div
                            className="alert alert-info"
                            role="alert"
                            style={{ minHeight: 300, userSelect: 'text', textAlign: 'left' }}
                          >
                            <pre style={{ color: '#000', background: 'transparent', border: 0 }}>
                              {querySql ?? 'Preview Query 를 눌러 SQL 을 확인하세요.'}
                            </pre>
                          </div>
                        </div>
                      )}
                      {tab === 'option' && (
                        <div className="tab-pane active">
                          <textarea
                            className="form-control"
                            rows={12}
                            value={serializeWidgetData(model)}
                            onChange={(e) => {
                              try {
                                setModel(parseWidgetData(e.target.value))
                                invalidatePreview()
                              } catch {
                                setError('JSON 형식이 올바르지 않습니다.')
                              }
                            }}
                          />
                        </div>
                      )}
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </ConfigEditorPane>
        </div>
      </div>
    </div>
  )
}

function datasetsLabel(datasetId: number | undefined, schema: DatasetData | null) {
  if (!datasetId) return ''
  return schema?.query.sql ? `Dataset #${datasetId}` : `Dataset #${datasetId}`
}
