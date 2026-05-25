import { useEffect, useMemo, useRef } from 'react'
import { echarts } from '../../legacy/echartsSetup'
import { aggregateToTableHtml, type AggregateResult } from '../../utils/aggregateApi'
import { buildEchartsOption, chartTypeLabel } from '../../utils/chartRender'
import type { WidgetDataModel } from '../../utils/widgetModel'

type WidgetChartViewProps = {
  chartType: string
  result: AggregateResult | null
  widgetConfig?: WidgetDataModel['config']
  widgetId?: number
  loading?: boolean
  error?: string | null
  height?: number
  emptyMessage?: string
}

type ViewMode = 'loading' | 'error' | 'empty' | 'table' | 'fallback-table' | 'chart'

function disposeChart(instanceRef: { current: echarts.ECharts | null }) {
  const inst = instanceRef.current
  instanceRef.current = null
  if (inst && !inst.isDisposed()) {
    inst.dispose()
  }
}

export function WidgetChartView({
  chartType,
  result,
  widgetConfig,
  widgetId: _widgetId,
  loading,
  error,
  height = 280,
  emptyMessage = '데이터가 없습니다.',
}: WidgetChartViewProps) {
  const chartHostRef = useRef<HTMLDivElement>(null)
  const tableHostRef = useRef<HTMLDivElement>(null)
  const instanceRef = useRef<echarts.ECharts | null>(null)

  const viewMode: ViewMode = useMemo(() => {
    if (loading) return 'loading'
    if (error) return 'error'
    if (!result?.data.length) return 'empty'
    if (chartType === 'table') return 'table'
    const option = buildEchartsOption(chartType, result, widgetConfig)
    if (!option) return 'fallback-table'
    return 'chart'
  }, [loading, error, result, chartType, widgetConfig])

  const showTable = viewMode === 'table' || viewMode === 'fallback-table'

  useEffect(() => {
    const el = chartHostRef.current
    if (viewMode !== 'chart' || !el || !result) {
      disposeChart(instanceRef)
      return
    }
    const option = buildEchartsOption(chartType, result, widgetConfig)
    if (!option) {
      disposeChart(instanceRef)
      return
    }

    disposeChart(instanceRef)
    instanceRef.current = echarts.init(el)
    instanceRef.current.setOption(option)
    const onResize = () => {
      if (instanceRef.current && !instanceRef.current.isDisposed()) {
        instanceRef.current.resize()
      }
    }
    window.addEventListener('resize', onResize)
    return () => {
      window.removeEventListener('resize', onResize)
      disposeChart(instanceRef)
    }
  }, [viewMode, chartType, result, widgetConfig])

  useEffect(() => {
    const el = tableHostRef.current
    if (!el) return
    if (showTable && result) {
      el.innerHTML = aggregateToTableHtml(result)
    } else {
      el.innerHTML = ''
    }
  }, [showTable, result])

  return (
    <div style={{ position: 'relative', minHeight: height, width: '100%' }}>
      {viewMode === 'loading' && (
        <div
          className="text-center"
          style={{
            position: 'absolute',
            inset: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'rgba(255,255,255,0.85)',
            zIndex: 2,
          }}
        >
          <span className="fa fa-refresh fa-spin fa-2x text-muted" aria-hidden />
        </div>
      )}

      {viewMode === 'error' && <p className="text-danger">{error}</p>}
      {viewMode === 'empty' && <p className="text-muted">{emptyMessage}</p>}

      {viewMode === 'fallback-table' && (
        <p className="text-warning" style={{ marginBottom: 8 }}>
          「{chartTypeLabel(chartType)}」은 React 뷰어에서 아직 전용 차트로 그리지 않습니다. 표로
          표시합니다.
        </p>
      )}

      <div
        ref={chartHostRef}
        style={{
          width: '100%',
          height,
          display: viewMode === 'chart' ? 'block' : 'none',
        }}
      />

      <div
        ref={tableHostRef}
        className="table-responsive"
        style={{
          display: showTable ? 'block' : 'none',
          marginTop: viewMode === 'fallback-table' ? 12 : 0,
        }}
      />
    </div>
  )
}
