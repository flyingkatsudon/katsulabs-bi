import { api } from '../api/client'
import type { WidgetDataModel } from './widgetModel'

export type AggregateColumn = {
  index: number
  aggType: string | null
  name: string
}

export type AggregateResult = {
  columnList: AggregateColumn[]
  data: string[][]
}

export type ExtraAggregateFilter = {
  col: string
  values: string[]
}

/** 레거시 getDimensionConfig + getDataSeries 와 동일한 cfg */
export function buildAggregateCfg(
  model: WidgetDataModel,
  extraFilters: ExtraAggregateFilter[] = [],
): string {
  const toDim = (arr: { col?: string; column?: string; type?: string; values?: unknown[] }[] | undefined) =>
    (arr ?? [])
      .map((e) => ({
        columnName: (e.col ?? e.column ?? '').trim(),
        filterType: 'eq' as const,
        values: Array.isArray(e.values) ? e.values.map(String) : [],
      }))
      .filter((d) => d.columnName.length > 0)

  const groupFilters = (model.filterGroups ?? []).flatMap((g) =>
    (g.filters as { col?: string; column?: string; values?: unknown[] }[] | undefined) ?? [],
  )

  const values: { column: string; aggType: string }[] = []
  for (const bucket of model.config.values ?? []) {
    for (const c of bucket.cols ?? []) {
      const col = c.col ?? c.column
      if (!col?.trim() && c.type !== 'exp') continue
      if (c.type === 'exp') {
        values.push({ column: c.alias ?? 'exp', aggType: 'sum' })
      } else if (col?.trim()) {
        values.push({ column: col.trim(), aggType: c.aggregate_type ?? 'sum' })
      }
    }
  }

  const boardDims = extraFilters
    .filter((f) => f.col.trim() && f.values.length > 0)
    .map((f) => ({
      columnName: f.col.trim(),
      filterType: 'eq' as const,
      values: f.values.map(String),
    }))

  return JSON.stringify({
    rows: toDim(model.config.keys as { col?: string; type?: string; values?: unknown[] }[]),
    columns: toDim(model.config.groups as { col?: string; type?: string; values?: unknown[] }[]),
    filters: [
      ...toDim(model.config.filters as { col?: string; type?: string; values?: unknown[] }[]),
      ...toDim(groupFilters),
      ...boardDims,
    ],
    values,
  })
}

export async function fetchAggregate(
  datasetId: number,
  model: WidgetDataModel,
  reload = false,
  extraFilters: ExtraAggregateFilter[] = [],
): Promise<AggregateResult> {
  return api.post<AggregateResult>('/api/v1/aggregate', {
    datasetId,
    datasourceId: model.datasource ?? null,
    query: {},
    cfg: buildAggregateCfg(model, extraFilters),
    reload,
  })
}

export async function fetchAggregateViewQuery(
  datasetId: number,
  model: WidgetDataModel,
): Promise<string> {
  const res = await api.post<{ sql: string }>('/api/v1/aggregate/view-query', {
    datasetId,
    datasourceId: model.datasource ?? null,
    query: {},
    cfg: buildAggregateCfg(model),
  })
  return res.sql
}

/** 집계 결과를 단순 HTML 테이블로 (table 차트 미리보기) */
export function aggregateToTableHtml(result: AggregateResult): string {
  if (!result.data.length) {
    return '<p class="text-muted">No Data</p>'
  }
  const headers = result.columnList.map((c) =>
    c.aggType ? `${c.aggType}(${c.name})` : c.name,
  )
  let html = '<table class="table table-bordered table-condensed table-striped"><thead><tr>'
  for (const h of headers) {
    html += `<th>${escapeHtml(h)}</th>`
  }
  html += '</tr></thead><tbody>'
  for (const row of result.data) {
    html += '<tr>'
    for (const cell of row) {
      html += `<td>${escapeHtml(cell ?? '')}</td>`
    }
    html += '</tr>'
  }
  html += '</tbody></table>'
  return html
}

function escapeHtml(s: string) {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}
