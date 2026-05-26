import type { DatasetData } from './datasetModel'
import { chartConfigZones } from './chartRender'
import type { WidgetDataModel } from './widgetModel'
import { toWidgetCol } from './widgetModel'

function firstDimensionColumn(schema: DatasetData): string | null {
  for (const d of schema.schema.dimension) {
    if (d.type === 'column' && d.column) return d.column
    if (d.type === 'level' && d.columns?.[0]?.column) return d.columns[0].column
  }
  return schema.selects.find((s) => !schema.schema.measure.some((m) => m.column === s)) ?? null
}

function firstMeasureColumn(schema: DatasetData): string | null {
  return schema.schema.measure[0]?.column ?? null
}

function secondMeasureColumn(schema: DatasetData): string | null {
  return schema.schema.measure[1]?.column ?? null
}

/** 데이터셋 스키마에 맞는 위젯 Row/Value 초기값 (새 위젯·데이터셋 변경 시) */
export function buildStarterWidgetConfig(
  schema: DatasetData,
  chartType: string,
): WidgetDataModel['config'] {
  const dim = firstDimensionColumn(schema)
  const m1 = firstMeasureColumn(schema)
  const m2 = secondMeasureColumn(schema)
  const keys = dim ? [toWidgetCol({ column: dim, type: 'column' })] : []
  const values = m1
    ? [{ cols: [toWidgetCol({ column: m1, type: 'column' }, true)], series_type: 'line' as const }]
    : [{ cols: [] }]

  switch (chartType) {
    case 'kpi':
    case 'gauge':
      return { chart_type: chartType, keys: [], groups: [], values }
    case 'pie':
    case 'funnel':
    case 'wordCloud':
    case 'boxplot':
    case 'pyramid':
      return { chart_type: chartType, keys, groups: [], values }
    case 'scatter':
      return {
        chart_type: chartType,
        keys,
        groups: [],
        values: [
          {
            cols: [
              ...(m1 ? [toWidgetCol({ column: m1, type: 'column' }, true)] : []),
              ...(m2 ? [toWidgetCol({ column: m2, type: 'column' }, true)] : []),
            ],
          },
        ],
      }
    case 'sankey':
    case 'heatMapTable':
    case 'treeMap':
    case 'sunburst':
    case 'themeRiver': {
      const dim2 =
        schema.schema.dimension[1]?.type === 'column'
          ? schema.schema.dimension[1].column
          : schema.selects[1] ?? dim
      const groups =
        dim2 && dim2 !== dim ? [toWidgetCol({ column: dim2, type: 'column' })] : []
      return { chart_type: chartType, keys, groups, values }
    }
    case 'contrast':
    case 'radar':
      return {
        chart_type: chartType,
        keys,
        groups: [],
        values: [
          {
            cols: [
              ...(m1 ? [toWidgetCol({ column: m1, type: 'column' }, true)] : []),
              ...(m2 ? [toWidgetCol({ column: m2, type: 'column' }, true)] : []),
            ],
          },
        ],
      }
    default:
      return { chart_type: chartType, keys, groups: [], values }
  }
}

/** 차트 유형에서 숨겨진 Row/Column/Value 매핑 제거 */
export function pruneWidgetConfigForChartType(
  config: WidgetDataModel['config'],
  chartType: string,
): WidgetDataModel['config'] {
  const zones = chartConfigZones(chartType)
  const values = config.values?.length ? [...config.values] : [{ cols: [] }]
  values[0] = {
    ...values[0],
    cols: zones.value ? [...(values[0].cols ?? [])] : [],
  }
  return {
    ...config,
    chart_type: chartType,
    keys: zones.row ? [...(config.keys ?? [])] : [],
    groups: zones.column ? [...(config.groups ?? [])] : [],
    values,
  }
}

/** 데이터셋·차트 유형 변경 시 Row/Column/Value 초기화 */
export function applyWidgetMappingReset(
  model: WidgetDataModel,
  schema: DatasetData,
  chartType?: string,
): WidgetDataModel {
  const ct = chartType ?? model.config.chart_type ?? 'table'
  return {
    ...model,
    datasource: schema.datasource,
    config: pruneWidgetConfigForChartType(buildStarterWidgetConfig(schema, ct), ct),
  }
}
