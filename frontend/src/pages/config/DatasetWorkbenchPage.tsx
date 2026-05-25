import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api, ApiError } from '../../api/client'
import type {
  DatasetDetail,
  DatasetPreview,
  DatasetSummary,
  DatasourceSummary,
  ServiceResult,
} from '../../api/types'
import { ConfigJsTree } from '../../components/config/ConfigJsTree'
import { DatasetSchemaPanel } from '../../components/dataset/DatasetSchemaPanel'
import { ConfigWorkbench } from '../../components/config/ConfigWorkbench'
import { FormAlerts } from '../../components/FormAlerts'
import {
  type DatasetData,
  emptyDatasetData,
  parseDatasetData,
  serializeDatasetData,
  splitDisplayName,
} from '../../utils/datasetModel'
import { buildCategoryTreeData, filterTreeList, type TreeListItem } from '../../utils/configTreeData'

type DatasetWorkbenchPageProps = {
  onUnauthorized: () => void
}

export function DatasetWorkbenchPage({ onUnauthorized }: DatasetWorkbenchPageProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedId = searchParams.get('id')
  const [keywords, setKeywords] = useState('')

  const [list, setList] = useState<DatasetSummary[]>([])
  const [datasources, setDatasources] = useState<DatasourceSummary[]>([])
  const [displayName, setDisplayName] = useState('Default Category/new_dataset')
  const [datasetData, setDatasetData] = useState<DatasetData>(() => emptyDatasetData(1))
  const [preview, setPreview] = useState<DatasetPreview | null>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [loadDataBusy, setLoadDataBusy] = useState(false)

  const isNew = selectedId === 'new'
  const numericId = selectedId && selectedId !== 'new' ? Number(selectedId) : null
  const editorOpen = isNew || numericId != null

  const categoryHints = useMemo(() => {
    const set = new Set(list.map((d) => d.categoryName ?? 'Default Category'))
    return [...set].sort()
  }, [list])

  const datasourceNameById = useMemo(() => {
    const m = new Map<number, string>()
    for (const d of datasources) m.set(d.id, d.name)
    return m
  }, [datasources])

  const treeInputs = useMemo(() => {
    const items: (TreeListItem & { datasourceName?: string })[] = list.map((d) => ({
      id: d.id,
      name: d.name,
      categoryName: d.categoryName ?? 'Default Category',
      datasourceName:
        d.datasourceId != null ? datasourceNameById.get(d.datasourceId) : undefined,
    }))
    return filterTreeList(items, keywords)
  }, [list, keywords, datasourceNameById])

  const treeData = useMemo(() => buildCategoryTreeData(treeInputs), [treeInputs])

  const loadList = useCallback(async () => {
    const [datasets, sources] = await Promise.all([
      api.get<DatasetSummary[]>('/api/v1/datasets'),
      api.get<DatasourceSummary[]>('/api/v1/datasources'),
    ])
    setList(datasets)
    setDatasources(sources)
  }, [])

  const loadDetail = useCallback(async (id: number) => {
    const detail = await api.get<DatasetDetail>(`/api/v1/datasets/${id}`)
    setDisplayName(`${detail.categoryName ?? 'Default Category'}/${detail.name}`)
    setDatasetData(parseDatasetData(detail.dataJson))
  }, [])

  useEffect(() => {
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const [datasets, sources] = await Promise.all([
          api.get<DatasetSummary[]>('/api/v1/datasets'),
          api.get<DatasourceSummary[]>('/api/v1/datasources'),
        ])
        setList(datasets)
        setDatasources(sources)
        if (isNew) {
          const dsId = sources[0]?.id ?? 1
          setDisplayName('Default Category/new_dataset')
          setDatasetData(emptyDatasetData(dsId, 'SELECT * FROM sales_fact_sample_flat'))
          setPreview(null)
        } else if (numericId != null && Number.isFinite(numericId)) {
          await loadDetail(numericId)
          setPreview(null)
        }
      } catch (e) {
        if (e instanceof ApiError && e.status === 401) onUnauthorized()
        else setError(e instanceof Error ? e.message : '로드 실패')
      } finally {
        setLoading(false)
      }
    })()
  }, [isNew, loadDetail, numericId, onUnauthorized])

  function selectItem(id: string | null) {
    setPreview(null)
    setMessage(null)
    setError(null)
    if (id) setSearchParams({ id })
    else setSearchParams({})
  }

  function getSelectedSummary() {
    if (numericId == null) return null
    return list.find((d) => d.id === numericId) ?? null
  }

  async function handleLoadData() {
    setLoadDataBusy(true)
    setError(null)
    setMessage(null)
    try {
      const result = await api.post<DatasetPreview>('/api/v1/datasets/preview-query', {
        datasourceId: datasetData.datasource,
        sql: datasetData.query.sql,
        limit: 50,
      })
      setPreview(result)
      setDatasetData((prev) => ({ ...prev, selects: result.columns }))
      setMessage('컬럼을 불러왔습니다.')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Load Data 실패')
    } finally {
      setLoadDataBusy(false)
    }
  }

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    const { categoryName, name } = splitDisplayName(displayName)
    if (!name) {
      setError('이름을 입력하세요 (Category/name).')
      return
    }
    const body = { name, categoryName, dataJson: serializeDatasetData(datasetData) }
    try {
      const result =
        isNew || numericId == null
          ? await api.post<ServiceResult>('/api/v1/datasets', body)
          : await api.put<ServiceResult>(`/api/v1/datasets/${numericId}`, body)
      if (result.status === '1') {
        setMessage('저장되었습니다.')
        await loadList()
        if (result.id != null) selectItem(String(result.id))
      } else setError(result.message)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onUnauthorized()
      else setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(id: number) {
    if (!window.confirm('삭제할까요?')) return
    const result = await api.delete<ServiceResult>(`/api/v1/datasets/${id}`)
    if (result.status === '1') {
      await loadList()
      selectItem(null)
    } else setError(result.message)
  }

  async function handleCopy() {
    const src = getSelectedSummary()
    if (!src) return
    const detail = await api.get<DatasetDetail>(`/api/v1/datasets/${src.id}`)
    const body = {
      name: `${src.name}_copy`,
      categoryName: src.categoryName ?? 'Default Category',
      dataJson: detail.dataJson ?? serializeDatasetData(datasetData),
    }
    const result = await api.post<ServiceResult>('/api/v1/datasets', body)
    if (result.status === '1') {
      await loadList()
      if (result.id != null) selectItem(String(result.id))
    }
  }

  function showInfo() {
    const src = getSelectedSummary()
    if (!src) {
      window.alert('데이터셋을 선택하세요.')
      return
    }
    window.alert(
      `ID: ${src.id}\nName: ${src.name}\nCategory: ${src.categoryName}\nUser: ${src.userName}`,
    )
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
    <ConfigWorkbench
      title="Dataset"
      icon="fa-cubes"
      toolbar={
        <>
          {toolbarIcon('fa-info', 'Information', showInfo, !numericId)}
          &nbsp;&nbsp;
          {toolbarIcon('fa-plus', 'New', () => selectItem('new'))}
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
        </>
      }
      sidebar={
        <>
          <div className="box-body">
            <input
              type="text"
              className="form-control"
              placeholder="Search"
              title="dsr:kylin ds:Bill"
              value={keywords}
              onChange={(e) => setKeywords(e.target.value)}
            />
          </div>
          <ConfigJsTree
            treeId="dataSetTreeID"
            treeData={treeData}
            selectedId={numericId != null ? String(numericId) : null}
            onSelectLeaf={(id) => selectItem(String(id))}
          />
        </>
      }
    >
      {loading && <p>로딩 중…</p>}
      {!loading && !editorOpen && (
        <div className="box box-default">
          <div className="box-body">
            <p>왼쪽 트리에서 데이터셋을 선택하거나 툴바에서 New 를 누르세요.</p>
          </div>
        </div>
      )}
      {!loading && editorOpen && (
        <div className="box" style={{ position: 'relative' }}>
          <div className="box-header with-border">
            <h3 className="box-title">{displayName || 'Dataset'}</h3>
            <div className="box-tools pull-right">
              {isNew ? <span className="label label-danger">NEW</span> : <span className="label label-info">EDIT</span>}
            </div>
          </div>
          <form className="box-body" onSubmit={handleSave}>
            <FormAlerts message={message} error={error} />
            <div className="form-horizontal">
              <div className="form-group">
                <label className="col-sm-2 control-label">Name</label>
                <div className="col-sm-10">
                  <input
                    id="DatasetName"
                    className="form-control"
                    list="dataset-category-list"
                    placeholder="Category/name"
                    value={displayName}
                    onChange={(e) => setDisplayName(e.target.value)}
                  />
                  <datalist id="dataset-category-list">
                    {categoryHints.map((c) => (
                      <option key={c} value={`${c}/`} />
                    ))}
                  </datalist>
                </div>
              </div>
              <div className="form-group">
                <label className="col-sm-2 control-label">Data source</label>
                <div className="col-sm-10">
                  <select
                    className="form-control"
                    value={datasetData.datasource}
                    onChange={(e) =>
                      setDatasetData((prev) => ({
                        ...prev,
                        datasource: Number(e.target.value),
                      }))
                    }
                  >
                    {datasources.map((d) => (
                      <option key={d.id} value={d.id}>
                        {d.name} ({d.type})
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="col-sm-2 control-label">SQL</label>
                <div className="col-sm-10">
                  <textarea
                    className="form-control"
                    rows={4}
                    value={datasetData.query.sql}
                    onChange={(e) =>
                      setDatasetData((prev) => ({
                        ...prev,
                        query: { sql: e.target.value },
                      }))
                    }
                  />
                </div>
              </div>
              <div className="form-group">
                <div className="col-sm-offset-2 col-sm-10">
                  <div className="btn-group">
                    <button
                      type="button"
                      className="btn btn-success"
                      disabled={loadDataBusy}
                      onClick={() => void handleLoadData()}
                    >
                      Load Data
                    </button>
                    <button
                      type="button"
                      className="btn btn-success dropdown-toggle"
                      data-toggle="dropdown"
                      aria-expanded="false"
                    >
                      <span className="caret" />
                    </button>
                    <ul className="dropdown-menu" role="menu">
                      <li>
                        <a
                          href="#from-cache"
                          onClick={(e) => {
                            e.preventDefault()
                            void handleLoadData()
                          }}
                        >
                          From cache
                        </a>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
              <div className="form-group">
                <label className="col-sm-2 control-label">Real time interval</label>
                <div className="col-sm-10">
                  <input
                    className="form-control"
                    placeholder="e.g. 60 (seconds)"
                    value={datasetData.interval ?? ''}
                    onChange={(e) =>
                      setDatasetData((prev) => ({ ...prev, interval: e.target.value }))
                    }
                  />
                </div>
              </div>
              {datasetData.selects.length > 0 && (
                <DatasetSchemaPanel data={datasetData} onChange={setDatasetData} />
              )}
            </div>
          </form>
          <div className="box-footer">
            <button type="button" className="btn btn-danger pull-right" onClick={() => selectItem(null)}>
              Cancel
            </button>
            <button
              type="button"
              className="btn btn-success pull-right"
              style={{ marginRight: 5 }}
              onClick={(e) => void handleSave(e as unknown as FormEvent)}
            >
              Save
            </button>
            {preview && (
              <div className="row" style={{ clear: 'both', paddingTop: 12 }}>
                <div className="col-md-12">
                  <div className="table-responsive" id="dataset_preview">
                    <table className="table table-bordered table-condensed">
                      <thead>
                        <tr>
                          {preview.columns.map((c) => (
                            <th key={c}>{c}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {preview.rows.map((row, i) => (
                          <tr key={i}>
                            {preview.columns.map((c) => (
                              <td key={c}>{String(row[c] ?? '')}</td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            )}
          </div>
          {loadDataBusy && (
            <div className="overlay">
              <i className="fa fa-spinner fa-spin" />
            </div>
          )}
        </div>
      )}
    </ConfigWorkbench>
  )
}
