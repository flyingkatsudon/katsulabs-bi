import { useState } from 'react'
import { chartConfigZones } from '../../utils/chartRender'
import { getChartGuide } from '../../utils/chartGuide'
import { parseWidgetSchemaDrag, type WidgetSchemaDragPayload } from '../../utils/widgetDrag'
import {
  AGGREGATE_TYPES,
  CHART_TYPES_WITH_SERIES,
  DEFAULT_AGGREGATE_TYPE,
  VALUE_SERIES_TYPES,
  type WidgetCol,
  type WidgetDataModel,
} from '../../utils/widgetModel'

type WidgetChartConfigProps = {
  model: WidgetDataModel
  onChange: (model: WidgetDataModel) => void
  onDropRow?: (field: WidgetSchemaDragPayload) => void
  onDropColumn?: (field: WidgetSchemaDragPayload) => void
  onDropValue?: (field: WidgetSchemaDragPayload) => void
}

function ColChip({
  o,
  onRemove,
}: {
  o: WidgetCol
  onRemove: () => void
}) {
  const label = o.type === 'exp' ? o.alias : `${o.col}${o.alias ? ` → ${o.alias}` : ''}`
  return (
    <span className="btn btn-default btn-sm" style={{ margin: '3px 3px', cursor: 'default' }}>
      {label}
      <i className="fa fa-times" style={{ marginLeft: 6, cursor: 'pointer' }} onClick={onRemove} />
    </span>
  )
}

function MeasureColChip({
  o,
  onRemove,
  onAggregateChange,
}: {
  o: WidgetCol
  onRemove: () => void
  onAggregateChange: (aggregateType: string) => void
}) {
  if (o.type === 'exp') {
    return (
      <span className="btn btn-default btn-sm" style={{ margin: '3px 3px', cursor: 'default' }}>
        {o.alias}
        <i className="fa fa-times" style={{ marginLeft: 6, cursor: 'pointer' }} onClick={onRemove} />
      </span>
    )
  }

  const agg = o.aggregate_type ?? DEFAULT_AGGREGATE_TYPE
  return (
    <span className="btn btn-default btn-sm" style={{ margin: '3px 3px', cursor: 'default' }}>
      <select
        className="input-sm"
        value={agg}
        title="집계 함수"
        style={{
          border: 'none',
          background: 'transparent',
          padding: 0,
          marginRight: 2,
          maxWidth: 88,
          cursor: 'pointer',
        }}
        onChange={(e) => onAggregateChange(e.target.value)}
        onClick={(e) => e.stopPropagation()}
      >
        {AGGREGATE_TYPES.map((t) => (
          <option key={t.value} value={t.value}>
            {t.label}
          </option>
        ))}
      </select>
      ({o.col}){o.alias ? ` → ${o.alias}` : ''}
      <i className="fa fa-times" style={{ marginLeft: 6, cursor: 'pointer' }} onClick={onRemove} />
    </span>
  )
}

function DropZone({
  label,
  cols,
  onRemove,
  hint,
  onDrop,
}: {
  label: string
  cols: WidgetCol[]
  onRemove: (index: number) => void
  hint?: string
  onDrop?: (field: WidgetSchemaDragPayload) => void
}) {
  const [dragOver, setDragOver] = useState(false)

  return (
    <div className="form-group">
      <label className="col-sm-2 control-label" title={hint}>
        {label}
        {hint && (
          <i className="fa fa-question-circle text-muted" style={{ marginLeft: 6 }} title={hint} />
        )}
      </label>
      <div className="col-sm-10">
        <div
          className="form-control"
          style={{
            minHeight: 35,
            height: 'auto',
            padding: '3px',
            borderColor: dragOver ? '#3c8dbc' : undefined,
            backgroundColor: dragOver ? '#f4f9fc' : undefined,
          }}
          onDragOver={(e) => {
            if (!onDrop) return
            e.preventDefault()
            e.dataTransfer.dropEffect = 'copy'
            setDragOver(true)
          }}
          onDragLeave={() => setDragOver(false)}
          onDrop={(e) => {
            e.preventDefault()
            setDragOver(false)
            if (!onDrop) return
            const field = parseWidgetSchemaDrag(e.dataTransfer)
            if (field) onDrop(field)
          }}
        >
          {cols.map((o, i) => (
            <ColChip key={i} o={o} onRemove={() => onRemove(i)} />
          ))}
          {cols.length === 0 && (
            <span className="text-muted">
              {onDrop ? '스키마 필드를 드래그하거나 클릭하여 추가' : '왼쪽 Dataset 스키마에서 클릭하여 추가'}
            </span>
          )}
        </div>
      </div>
    </div>
  )
}

/** Row / Column / Value 영역 — 차트 유형별 레거시 configRule 반영 */
export function WidgetChartConfig({
  model,
  onChange,
  onDropRow,
  onDropColumn,
  onDropValue,
}: WidgetChartConfigProps) {
  const config = model.config
  const chartType = config.chart_type ?? 'table'
  const zones = chartConfigZones(chartType)
  const guide = getChartGuide(chartType)
  const [valueDragOver, setValueDragOver] = useState(false)

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
          onDrop={onDropRow}
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
          onDrop={onDropColumn}
          onRemove={(i) => {
            const groups = [...(config.groups ?? [])]
            groups.splice(i, 1)
            updateConfig({ groups })
          }}
        />
      )}
      {zones.value && (
        <div className="form-group">
          <label className="col-sm-2 control-label" title="Measure — 집계할 컬럼 (드롭다운에서 sum/avg/count 등 선택)">
            Value
            <i
              className="fa fa-question-circle text-muted"
              style={{ marginLeft: 6 }}
              title="Measure — 집계할 컬럼 (드롭다운에서 sum/avg/count 등 선택)"
            />
          </label>
          <div className="col-sm-10">
            <div
              className="form-control"
              style={{
                minHeight: 35,
                height: 'auto',
                padding: '3px',
                borderColor: valueDragOver ? '#3c8dbc' : undefined,
                backgroundColor: valueDragOver ? '#f4f9fc' : undefined,
              }}
              onDragOver={(e) => {
                if (!onDropValue) return
                e.preventDefault()
                e.dataTransfer.dropEffect = 'copy'
                setValueDragOver(true)
              }}
              onDragLeave={() => setValueDragOver(false)}
              onDrop={(e) => {
                e.preventDefault()
                setValueDragOver(false)
                if (!onDropValue) return
                const field = parseWidgetSchemaDrag(e.dataTransfer)
                if (field) onDropValue(field)
              }}
            >
              {(config.values?.[0]?.cols ?? []).map((o, i) => (
                <MeasureColChip
                  key={i}
                  o={o}
                  onRemove={() => {
                    const values = config.values?.length ? [...config.values] : [{ cols: [] }]
                    const cols = [...(values[0].cols ?? [])]
                    cols.splice(i, 1)
                    values[0] = { ...values[0], cols }
                    updateConfig({ values })
                  }}
                  onAggregateChange={(aggregateType) => {
                    const values = config.values?.length ? [...config.values] : [{ cols: [] }]
                    const cols = [...(values[0].cols ?? [])]
                    cols[i] = { ...cols[i], aggregate_type: aggregateType }
                    values[0] = { ...values[0], cols }
                    updateConfig({ values })
                  }}
                />
              ))}
              {(config.values?.[0]?.cols ?? []).length === 0 && (
                <span className="text-muted">
                  {onDropValue
                    ? '스키마 Measure/Dimension을 드래그하거나 클릭하여 추가'
                    : '왼쪽 Dataset 스키마 Measure에서 클릭하여 추가'}
                </span>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
