import { useCallback, useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api/client'
import type { WidgetDetail } from '../../api/types'
import { WidgetChartView } from '../widget/WidgetChartView'
import { fetchAggregate, type AggregateResult, type ExtraAggregateFilter } from '../../utils/aggregateApi'
import { validateWidgetForAggregate } from '../../utils/chartGuide'
import { parseWidgetData, type WidgetDataModel } from '../../utils/widgetModel'

type DashboardWidgetBoxProps = {
  widgetId: number
  name: string
  height?: number
  boardFilters?: ExtraAggregateFilter[]
  canEditWidget?: boolean
  /** 페이지 최초 진입 시 1회 자동 집계 */
  autoSyncOnMount?: boolean
  /** 부모가 증가시키면 집계 재실행 (전체 새로고침) */
  syncAllToken?: number
}

const IDLE_HINT = '새로고침 버튼을 눌러 차트 데이터를 조회하세요.'

export function DashboardWidgetBox({
  widgetId,
  name,
  height = 300,
  boardFilters = [],
  canEditWidget = true,
  autoSyncOnMount = true,
  syncAllToken = 0,
}: DashboardWidgetBoxProps) {
  const [refreshing, setRefreshing] = useState(false)
  const [metaLoading, setMetaLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [chartType, setChartType] = useState('table')
  const [widgetConfig, setWidgetConfig] = useState<WidgetDataModel['config']>()
  const [aggregate, setAggregate] = useState<AggregateResult | null>(null)
  const [synced, setSynced] = useState(false)
  const boardFiltersRef = useRef(boardFilters)
  boardFiltersRef.current = boardFilters
  const initialSyncDoneRef = useRef(false)
  const lastSyncAllTokenRef = useRef(0)

  const loadWidgetMeta = useCallback(async () => {
    setMetaLoading(true)
    setError(null)
    try {
      const detail = await api.get<WidgetDetail>(`/api/v1/widgets/${widgetId}`)
      const parsed = parseWidgetData(detail.dataJson)
      const ct = parsed.config.chart_type ?? 'table'
      setChartType(ct)
      setWidgetConfig(parsed.config)
      if (!parsed.datasetId) {
        setError('위젯에 Dataset 이 연결되어 있지 않습니다.')
      } else {
        const validation = validateWidgetForAggregate(ct, parsed.config)
        if (validation) {
          setError(validation)
        }
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : '위젯 설정 로드 실패')
    } finally {
      setMetaLoading(false)
    }
  }, [widgetId])

  const syncData = useCallback(async () => {
    setRefreshing(true)
    setError(null)
    try {
      const detail = await api.get<WidgetDetail>(`/api/v1/widgets/${widgetId}`)
      const parsed = parseWidgetData(detail.dataJson)
      const dsId = parsed.datasetId
      if (!dsId) {
        setError('위젯에 Dataset 이 연결되어 있지 않습니다.')
        setAggregate(null)
        setSynced(false)
        return
      }
      const ct = parsed.config.chart_type ?? 'table'
      setChartType(ct)
      setWidgetConfig(parsed.config)
      const validation = validateWidgetForAggregate(ct, parsed.config)
      if (validation) {
        setError(validation)
        setAggregate(null)
        setSynced(false)
        return
      }
      const result = await fetchAggregate(dsId, parsed, true, boardFiltersRef.current)
      setAggregate(result)
      setSynced(true)
    } catch (e) {
      setError(e instanceof Error ? e.message : '위젯 데이터 로드 실패')
      setAggregate(null)
      setSynced(false)
    } finally {
      setRefreshing(false)
    }
  }, [widgetId])

  useEffect(() => {
    setSynced(false)
    setAggregate(null)
    initialSyncDoneRef.current = false
    lastSyncAllTokenRef.current = 0
    void loadWidgetMeta()
  }, [widgetId, loadWidgetMeta])

  useEffect(() => {
    if (metaLoading || !autoSyncOnMount || initialSyncDoneRef.current) return
    if (error) return
    initialSyncDoneRef.current = true
    void syncData()
  }, [metaLoading, error, autoSyncOnMount, syncData])

  useEffect(() => {
    if (syncAllToken <= 0 || syncAllToken === lastSyncAllTokenRef.current) return
    lastSyncAllTokenRef.current = syncAllToken
    void syncData()
  }, [syncAllToken, syncData])

  const bodyHeight = Math.max(height - 60, 180)

  return (
    <div className="box box-solid" style={{ marginBottom: 0 }}>
      <div className="box-header with-border">
        <span className="fa fa-bar-chart-o" aria-hidden />
        <h3 className="box-title">{name}</h3>
        <div className="box-tools pull-right">
          <button
            type="button"
            className="btn btn-box-tool"
            title="차트 데이터 새로고침"
            disabled={metaLoading || refreshing}
            onClick={() => void syncData()}
          >
            <span className={`fa fa-refresh ${refreshing ? 'fa-spin' : ''}`} aria-hidden />
          </button>
          {canEditWidget && (
            <Link
              to={`/widgets?id=${widgetId}`}
              className="btn btn-box-tool"
              title="위젯 편집"
            >
              <span className="fa fa-wrench" aria-hidden />
            </Link>
          )}
        </div>
      </div>
      <div className="box-body" style={{ minHeight: bodyHeight }}>
        <WidgetChartView
          chartType={chartType}
          result={aggregate}
          widgetConfig={widgetConfig}
          widgetId={widgetId}
          loading={metaLoading || refreshing}
          error={error}
          height={bodyHeight}
          emptyMessage={synced ? '데이터가 없습니다.' : IDLE_HINT}
        />
      </div>
    </div>
  )
}
