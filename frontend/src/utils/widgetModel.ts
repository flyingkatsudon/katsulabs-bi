export type WidgetCol = {
  type?: string
  col?: string
  column?: string
  alias?: string
  aggregate_type?: string
  exp?: string
}

export type WidgetValueBucket = {
  cols: WidgetCol[]
  series_type?: string
}

export const AGGREGATE_TYPES = [
  { value: 'sum', label: 'Sum' },
  { value: 'avg', label: 'Avg' },
  { value: 'min', label: 'Min' },
  { value: 'max', label: 'Max' },
  { value: 'count', label: 'Count' },
  { value: 'distinct', label: 'Distinct' },
] as const

export type AggregateType = (typeof AGGREGATE_TYPES)[number]['value']

export const DEFAULT_AGGREGATE_TYPE: AggregateType = 'sum'

/** 레거시 value_series_types (line/bar 계열) */
export const VALUE_SERIES_TYPES = [
  { value: 'line', label: 'Line' },
  { value: 'arealine', label: 'Area Line' },
  { value: 'stackline', label: 'Stacked Line' },
  { value: 'percentline', label: 'Percent Line' },
  { value: 'bar', label: 'Bar' },
  { value: 'stackbar', label: 'Stacked Bar' },
  { value: 'percentbar', label: 'Percent Bar' },
  { value: 'polarbar', label: 'Polar Bar' },
  { value: 'rainfall', label: 'Rainfall' },
] as const

export const CHART_TYPES_WITH_SERIES = new Set(['line', 'contrast', 'boxplot', 'themeRiver', 'pareto'])

export type WidgetDataModel = {
  config: {
    chart_type?: string
    keys?: WidgetCol[]
    groups?: WidgetCol[]
    values?: WidgetValueBucket[]
    [key: string]: unknown
  }
  datasetId?: number
  datasource?: number
  query?: { sql?: string }
  expressions?: WidgetCol[]
  filterGroups?: { id?: string; group: string; filters?: unknown[] }[]
}

export { CHART_TYPES, CHART_REACT_RENDER, chartTypeLabel } from './chartRender'

export function parseWidgetData(json: string | null | undefined): WidgetDataModel {
  if (!json) {
    return defaultWidgetData(1, 'table')
  }
  const data = JSON.parse(json) as WidgetDataModel
  if (!data.config) data.config = { chart_type: 'table' }
  if (!data.config.chart_type) data.config.chart_type = 'table'
  if (!data.expressions) data.expressions = []
  if (!data.filterGroups) data.filterGroups = []
  if (!data.config.keys) data.config.keys = []
  if (!data.config.groups) data.config.groups = []
  if (!data.config.values?.[0]) data.config.values = [{ cols: [], series_type: 'line' }]
  if (!data.config.values[0].cols) data.config.values[0].cols = []
  if (!data.config.values[0].series_type && CHART_TYPES_WITH_SERIES.has(data.config.chart_type ?? '')) {
    data.config.values[0].series_type = 'line'
  }
  return data
}

export function defaultWidgetData(datasetId: number, chartType: string): WidgetDataModel {
  return {
    config: {
      chart_type: chartType,
      keys: [],
      groups: [],
      values: [{ cols: [], series_type: chartType === 'line' ? 'line' : undefined }],
    },
    datasetId,
    expressions: [],
    filterGroups: [],
  }
}

export function serializeWidgetData(data: WidgetDataModel): string {
  return JSON.stringify(data, null, 2)
}

export function buildWidgetDataJson(
  datasetId: number,
  chartType: string,
  existing?: string | null,
): string {
  const data = existing ? parseWidgetData(existing) : defaultWidgetData(datasetId, chartType)
  data.datasetId = datasetId
  data.config.chart_type = chartType
  return serializeWidgetData(data)
}

export function formatWidgetDisplayName(categoryName: string | null, name: string): string {
  const cat = categoryName?.trim() || 'Default Category'
  return `${cat}/${name}`
}

export function splitWidgetDisplayName(display: string): { categoryName: string; name: string } {
  const idx = display.lastIndexOf('/')
  if (idx < 0) {
    return { categoryName: 'Default Category', name: display.trim() }
  }
  return {
    categoryName: display.substring(0, idx).trim() || 'Default Category',
    name: display.substring(idx + 1).trim(),
  }
}

export function toWidgetCol(
  field: { column?: string; col?: string; alias?: string; type?: string; exp?: string; aggregate_type?: string },
  asMeasure = false,
  aggregateType: AggregateType = DEFAULT_AGGREGATE_TYPE,
): WidgetCol {
  const col = field.column ?? field.col ?? ''
  if (field.type === 'exp' || field.exp) {
    return { type: 'exp', alias: field.alias, exp: field.exp }
  }
  return {
    type: 'column',
    col,
    alias: field.alias,
    aggregate_type: asMeasure ? field.aggregate_type ?? aggregateType : undefined,
  }
}
