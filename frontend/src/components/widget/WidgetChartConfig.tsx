import { chartConfigZones } from '../../utils/chartRender'
import { getChartGuide } from '../../utils/chartGuide'
import {
  CHART_TYPES_WITH_SERIES,
  VALUE_SERIES_TYPES,
  type WidgetCol,
  type WidgetDataModel,
} from '../../utils/widgetModel'

type WidgetChartConfigProps = {
  model: WidgetDataModel
  onChange: (model: WidgetDataModel) => void
}

function ColChip({
  o,
  onRemove,
}: {
  o: WidgetCol
  onRemove: () => void
}) {
  const label =
    o.type === 'exp'
      ? o.alias
      : `${o.aggregate_type ?? 'sum'}(${o.col})${o.alias ? ` → ${o.alias}` : ''}`
  return (
    <span className="btn btn-default btn-sm" style={{ margin: '3px 3px', cursor: 'default' }}>
      {label}
      <i className="fa fa-times" style={{ marginLeft: 6, cursor: 'pointer' }} onClick={onRemove} />
    </span>
  )
}

function DropZone({
  label,
  cols,
  onRemove,
  hint,
}: {
  label: string
  cols: WidgetCol[]
  onRemove: (index: number) => void
  hint?: string
}) {
  return (
    <div className="form-group">
      <label className="col-sm-2 control-label" title={hint}>
        {label}
        {hint && (
          <i className="fa fa-question-circle text-muted" style={{ marginLeft: 6 }} title={hint} />
        )}
      </label>
      <div className="col-sm-10">
        <div className="form-control" style={{ minHeight: 35, height: 'auto', padding: '3px' }}>
          {cols.map((o, i) => (
            <ColChip key={i} o={o} onRemove={() => onRemove(i)} />
          ))}
          {cols.length === 0 && <span className="text-muted">왼쪽 Dataset 스키마에서 클릭하여 추가</span>}
        </div>
      </div>
    </div>
  )
}

/** Row / Column / Value 영역 — 차트 유형별 레거시 configRule 반영 */
export function WidgetChartConfig({ model, onChange }: WidgetChartConfigProps) {
  const config = model.config
  const chartType = config.chart_type ?? 'table'
  const zones = chartConfigZones(chartType)
  const guide = getChartGuide(chartType)

  function updateConfig(patch: Partial<WidgetDataModel['config']>) {
    onChange({ ...model, config: { ...config, ...patch } })
  }

  return (
    <div className="form-horizontal">
      {CHART_TYPES_WITH_SERIES.has(chartType) && zones.value && (
        <div className="form-group">
          <label className="col-sm-2 control-label">Series type</label>
          <div className="col-sm-10">
            <select
              className="form-control"
              style={{ maxWidth: 220 }}
              value={config.values?.[0]?.series_type ?? 'line'}
              onChange={(e) => {
                const values = config.values?.length ? [...config.values] : [{ cols: [] }]
                values[0] = { ...values[0], series_type: e.target.value }
                updateConfig({ values })
              }}
            >
              {VALUE_SERIES_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
          </div>
        </div>
      )}
      {zones.row && (
        <DropZone
          label="Row"
          hint={guide.steps[0]}
          cols={config.keys ?? []}
          onRemove={(i) => {
            const keys = [...(config.keys ?? [])]
            keys.splice(i, 1)
            updateConfig({ keys })
          }}
        />
      )}
      {zones.column && (
        <DropZone
          label="Column"
          hint="Column(그룹) — Sankey·히트맵 등에서 두 번째 차원"
          cols={config.groups ?? []}
          onRemove={(i) => {
            const groups = [...(config.groups ?? [])]
            groups.splice(i, 1)
            updateConfig({ groups })
          }}
        />
      )}
      {zones.value && (
        <DropZone
          label="Value"
          hint="Measure 클릭 — sum(매출) 등 집계할 숫자 컬럼"
          cols={config.values?.[0]?.cols ?? []}
          onRemove={(i) => {
            const values = config.values?.length ? [...config.values] : [{ cols: [] }]
            const cols = [...(values[0].cols ?? [])]
            cols.splice(i, 1)
            values[0] = { cols }
            updateConfig({ values })
          }}
        />
      )}
    </div>
  )
}
