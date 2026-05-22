import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api, ApiError } from '../../api/client'
import type { DatasourceDetail, DatasourceSummary, ServiceResult } from '../../api/types'
import { ConfigListActions } from '../../components/config/ConfigListActions'
import { ConfigWorkbench } from '../../components/config/ConfigWorkbench'
import { LegacyEditorFrame, LegacyEditorLink } from '../../components/config/LegacyEditorLink'
import { ConfigEditorPane, type ConfigLoadIssue } from '../../components/config/ConfigEditorPane'
import { FormAlerts } from '../../components/FormAlerts'
import { parseConfigResourceId } from '../../utils/parseConfigResourceId'
import { resolveConfigLoadError } from '../../utils/configResourceLoad'
import {
  buildJdbcConfig,
  DEFAULT_JDBC,
  parseJdbcConfig,
  type JdbcConfigFields,
} from '../../utils/jdbcConfig'

type DatasourceWorkbenchPageProps = {
  onSessionExpired: () => void
}

export function DatasourceWorkbenchPage({ onSessionExpired }: DatasourceWorkbenchPageProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedId = searchParams.get('id')

  const [list, setList] = useState<DatasourceSummary[]>([])
  const [name, setName] = useState('')
  const [type, setType] = useState('jdbc')
  const [jdbc, setJdbc] = useState<JdbcConfigFields>({ ...DEFAULT_JDBC })
  const [showAdvancedJson, setShowAdvancedJson] = useState(false)
  const [configJson, setConfigJson] = useState(buildJdbcConfig(DEFAULT_JDBC))
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [showLegacyFrame, setShowLegacyFrame] = useState(false)

  const [loadIssue, setLoadIssue] = useState<ConfigLoadIssue>(null)
  const resource = useMemo(() => parseConfigResourceId(selectedId), [selectedId])
  const isNew = resource.kind === 'new'
  const numericId = resource.kind === 'edit' ? resource.id : null

  const loadList = useCallback(async () => {
    setList(await api.get<DatasourceSummary[]>('/api/v1/datasources'))
  }, [])

  const loadDetail = useCallback(
    async (id: number) => {
      const detail = await api.get<DatasourceDetail>(`/api/v1/datasources/${id}`)
      setName(detail.name)
      setType(detail.type)
      const fields = parseJdbcConfig(detail.configJson)
      setJdbc(fields)
      setConfigJson(detail.configJson ?? buildJdbcConfig(fields))
    },
    [],
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
          setName('')
          setType('jdbc')
          setJdbc({ ...DEFAULT_JDBC })
          setConfigJson(buildJdbcConfig(DEFAULT_JDBC))
        } else if (resource.kind === 'edit') {
          await loadDetail(resource.id)
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
  }, [resource.kind, numericId, isNew, loadDetail, loadList, onSessionExpired])

  function selectItem(id: string | null) {
    setMessage(null)
    setError(null)
    setLoadIssue(null)
    if (id) setSearchParams({ id })
    else setSearchParams({})
  }

  function payload() {
    const json = type === 'jdbc' && !showAdvancedJson ? buildJdbcConfig(jdbc) : configJson
    return { name: name.trim(), type, configJson: json }
  }

  async function handleTest(e?: FormEvent) {
    e?.preventDefault()
    setMessage(null)
    setError(null)
    try {
      const result = await api.post<ServiceResult>('/api/v1/datasources/test', payload())
      if (result.status === '1') setMessage(result.message)
      else setError(result.message)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '테스트 실패')
    }
  }

  async function handleSave(e: FormEvent) {
    e.preventDefault()
    setMessage(null)
    setError(null)
    if (!name.trim()) {
      setError('이름을 입력하세요.')
      return
    }
    try {
      const result =
        isNew || numericId == null
          ? await api.post<ServiceResult>('/api/v1/datasources', payload())
          : await api.put<ServiceResult>(`/api/v1/datasources/${numericId}`, payload())
      if (result.status === '1') {
        setMessage('저장되었습니다.')
        await loadList()
        const newId = result.id ?? numericId
        if (newId != null) selectItem(String(newId))
      } else setError(result.message)
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) onSessionExpired()
      else setError(err instanceof Error ? err.message : '저장 실패')
    }
  }

  async function handleDelete(id = numericId) {
    if (id == null) return
    if (!window.confirm('삭제할까요?')) return
    const result = await api.delete<ServiceResult>(`/api/v1/datasources/${id}`)
    if (result.status === '1') {
      await loadList()
      if (numericId === id) selectItem(null)
    } else setError(result.message)
  }

  const legacyHash =
    isNew ? '/config/datasource/' : numericId != null ? `/config/datasource/${numericId}` : ''

  return (
    <ConfigWorkbench
      title="Data Source"
      icon="fa-database"
      toolbar={
        <i
          className="fa fa-plus toolbar-icon"
          style={{ cursor: 'pointer' }}
          title="New"
          onClick={() => selectItem('new')}
        />
      }
      sidebar={
        <>
          <div className="box-body no-padding">
            <ul className="nav nav-pills nav-stacked">
              {list.map((o) => (
                <li key={o.id} className={selectedId === String(o.id) ? 'active' : undefined}>
                  <a
                    href={`?id=${o.id}`}
                    onClick={(e) => {
                      e.preventDefault()
                      selectItem(String(o.id))
                    }}
                  >
                    {o.name}
                    <ConfigListActions
                      onInfo={() =>
                        window.alert(
                          `ID: ${o.id}\nName: ${o.name}\nType: ${o.type}\nUser: ${o.userName}`,
                        )
                      }
                      onCopy={() => {
                        selectItem('new')
                        setName(`${o.name}_copy`)
                        setType(o.type)
                      }}
                      onEdit={() => selectItem(String(o.id))}
                      onDelete={() => void handleDelete(o.id)}
                    />
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </>
      }
    >
      <ConfigEditorPane
        loading={loading}
        resource={resource}
        loadIssue={loadIssue}
        resourceLabel="데이터소스"
        listPath="/datasources"
        idleHint="왼쪽 목록에서 데이터소스를 선택하거나 새로 만듭니다."
        onBackToList={() => selectItem(null)}
      >
        <div className="box box-primary">
          <div className="box-header with-border">
            <h3 className="box-title">{isNew ? '새 데이터소스' : name}</h3>
            <div className="box-tools pull-right">
              {isNew && <span className="label label-danger">NEW</span>}
              {!isNew && <span className="label label-info">EDIT</span>}
              {legacyHash && <LegacyEditorLink hashPath={legacyHash} />}
            </div>
          </div>
          <form className="box-body form-horizontal" onSubmit={handleSave}>
            <FormAlerts message={message} error={error} />
            <div className="form-group">
              <label className="col-sm-2 control-label">Provider</label>
              <div className="col-sm-10">
                <select className="form-control" value={type} onChange={(e) => setType(e.target.value)}>
                  <option value="jdbc">jdbc</option>
                </select>
              </div>
            </div>
            <div className="form-group">
              <label className="col-sm-2 control-label">이름</label>
              <div className="col-sm-10">
                <input className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
              </div>
            </div>
            {type === 'jdbc' && !showAdvancedJson && (
              <>
                <div className="form-group">
                  <label className="col-sm-2 control-label">Driver</label>
                  <div className="col-sm-10">
                    <input
                      className="form-control"
                      value={jdbc.driver}
                      onChange={(e) => setJdbc({ ...jdbc, driver: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label className="col-sm-2 control-label">JDBC URL</label>
                  <div className="col-sm-10">
                    <input
                      className="form-control"
                      value={jdbc.jdbcurl}
                      onChange={(e) => setJdbc({ ...jdbc, jdbcurl: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label className="col-sm-2 control-label">Username</label>
                  <div className="col-sm-10">
                    <input
                      className="form-control"
                      value={jdbc.username}
                      onChange={(e) => setJdbc({ ...jdbc, username: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label className="col-sm-2 control-label">Password</label>
                  <div className="col-sm-10">
                    <input
                      type="password"
                      className="form-control"
                      value={jdbc.password}
                      onChange={(e) => setJdbc({ ...jdbc, password: e.target.value })}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <div className="col-sm-offset-2 col-sm-10">
                    <label className="checkbox-inline">
                      <input
                        type="checkbox"
                        checked={jdbc.pooled}
                        onChange={(e) => setJdbc({ ...jdbc, pooled: e.target.checked })}
                      />{' '}
                      Pooled
                    </label>
                    <label className="checkbox-inline">
                      <input
                        type="checkbox"
                        checked={jdbc.aggregateProvider}
                        onChange={(e) => setJdbc({ ...jdbc, aggregateProvider: e.target.checked })}
                      />{' '}
                      Aggregate Provider
                    </label>
                  </div>
                </div>
              </>
            )}
            <div className="form-group">
              <div className="col-sm-offset-2 col-sm-10">
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => {
                    setShowAdvancedJson(!showAdvancedJson)
                    if (!showAdvancedJson) setConfigJson(buildJdbcConfig(jdbc))
                  }}
                >
                  {showAdvancedJson ? '폼 편집' : '고급 JSON'}
                </button>
              </div>
            </div>
            {showAdvancedJson && (
              <div className="form-group">
                <label className="col-sm-2 control-label">config</label>
                <div className="col-sm-10">
                  <textarea
                    className="form-control"
                    rows={8}
                    value={configJson}
                    onChange={(e) => setConfigJson(e.target.value)}
                  />
                </div>
              </div>
            )}
            <div className="form-group">
              <div className="col-sm-offset-2 col-sm-10">
                <button type="button" className="btn btn-success" onClick={() => void handleTest()}>
                  Test
                </button>
                <button type="submit" className="btn btn-success" style={{ marginLeft: 8 }}>
                  Save
                </button>
                {!isNew && (
                  <button
                    type="button"
                    className="btn btn-danger pull-right"
                    onClick={() => void handleDelete()}
                  >
                    Delete
                  </button>
                )}
                {legacyHash && (
                  <button
                    type="button"
                    className="btn btn-info pull-right"
                    style={{ marginRight: 8 }}
                    onClick={() => setShowLegacyFrame((v) => !v)}
                  >
                    {showLegacyFrame ? '클래식 닫기' : '클래식 편집기'}
                  </button>
                )}
              </div>
            </div>
          </form>
        </div>
      </ConfigEditorPane>
      {showLegacyFrame && legacyHash && <LegacyEditorFrame hashPath={legacyHash} />}
    </ConfigWorkbench>
  )
}
