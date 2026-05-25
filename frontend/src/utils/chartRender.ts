import type { EChartsOption } from 'echarts'
import type { AggregateColumn, AggregateResult } from './aggregateApi'
import type { WidgetDataModel } from './widgetModel'

/** 지도·Fusion 등 — React 미지원, 레거시 편집기 안내 */
export const CHART_LEGACY_ONLY = new Set([
  'googleMap',
  'wordBubble',
  'fusionganttcharts',
  'heatMapCalendar',
  'relation',
  'liquidFill',
  'areaMap',
  'chinaMap',
  'chinaMapBmap',
  'map',
])

export function legacyWidgetEditorUrl(widgetId?: number): string {
  return widgetId ? `/widgets?id=${widgetId}` : '/widgets?id=new'
}

/** React 뷰어/미리보기에서 ECharts 로 그릴 수 있는 유형 */
/** 레거시 widgetCtrl configRule: 0=숨김, 그 외=표시 */
const CHART_CONFIG_RULES: Record<string, { keys: number; groups: number; values: number }> = {
  line: { keys: 2, groups: -1, values: 2 },
  pie: { keys: 2, groups: -1, values: 2 },
  kpi: { keys: 0, groups: 0, values: 1 },
  table: { keys: -1, groups: -1, values: -1 },
  funnel: { keys: -1, groups: 0, values: 2 },
  sankey: { keys: 2, groups: 2, values: 1 },
  radar: { keys: 2, groups: -1, values: 2 },
  scatter: { keys: 2, groups: -1, values: 2 },
  gauge: { keys: 0, groups: 0, values: 1 },
  wordCloud: { keys: 2, groups: 0, values: 1 },
  treeMap: { keys: 2, groups: 0, values: 1 },
  heatMapCalendar: { keys: 1, groups: 0, values: 1 },
  heatMapTable: { keys: 2, groups: 2, values: 1 },
  liquidFill: { keys: 0, groups: 0, values: 1 },
  contrast: { keys: 1, groups: 0, values: 2 },
  relation: { keys: 2, groups: 2, values: 1 },
  googleMap: { keys: 2, groups: -1, values: 2 },
  wordBubble: { keys: 1, groups: 0, values: 1 },
  fusionganttcharts: { keys: 2, groups: -1, values: 2 },
  boxplot: { keys: 2, groups: -1, values: 2 },
  themeRiver: { keys: 2, groups: -1, values: 2 },
  sunburst: { keys: 2, groups: -1, values: 2 },
  parallel: { keys: 2, groups: -1, values: 2 },
  pyramid: { keys: 2, groups: -1, values: 2 },
  pareto: { keys: 2, groups: -1, values: 2 },
}

export function chartConfigZones(chartType: string): {
  row: boolean
  column: boolean
  value: boolean
} {
  const rule = CHART_CONFIG_RULES[chartType] ?? { keys: -1, groups: -1, values: -1 }
  return {
    row: rule.keys !== 0,
    column: rule.groups !== 0,
    value: rule.values !== 0,
  }
}

export const CHART_REACT_RENDER = new Set([
  'table',
  'line',
  'pie',
  'kpi',
  'scatter',
  'contrast',
  'funnel',
  'gauge',
  'radar',
  'sankey',
  'wordCloud',
  'treeMap',
  'sunburst',
  'boxplot',
  'themeRiver',
  'parallel',
  'pyramid',
  'pareto',
  'heatMapTable',
])

function splitColumns(columnList: AggregateColumn[]) {
  const dimIdx: number[] = []
  const measureIdx: number[] = []
  columnList.forEach((c, i) => {
    if (c.aggType) measureIdx.push(i)
    else dimIdx.push(i)
  })
  return { dimIdx, measureIdx }
}

function cell(row: string[], idx: number): string {
  return row[idx] ?? ''
}

function num(row: string[], idx: number): number {
  const n = Number(row[idx])
  return Number.isFinite(n) ? n : 0
}

function dimLabel(row: string[], dimIdx: number[]): string {
  return dimIdx.map((i) => cell(row, i)).join('-')
}

function lineBarSeriesType(seriesType: string | undefined): {
  type: 'line' | 'bar'
  areaStyle?: Record<string, unknown>
  stack?: string
} {
  switch (seriesType) {
    case 'bar':
    case 'stackbar':
    case 'percentbar':
    case 'polarbar':
    case 'rainfall':
      return { type: 'bar', stack: seriesType.startsWith('stack') || seriesType === 'percentbar' ? 'total' : undefined }
    case 'arealine':
    case 'stackline':
    case 'percentline':
      return {
        type: 'line',
        areaStyle: {},
        stack: seriesType.startsWith('stack') || seriesType === 'percentline' ? 'total' : undefined,
      }
    default:
      return { type: 'line', smooth: true } as { type: 'line'; smooth: boolean }
  }
}

export function buildEchartsOption(
  chartType: string,
  result: AggregateResult,
  widgetConfig?: WidgetDataModel['config'],
): EChartsOption | null {
  if (CHART_LEGACY_ONLY.has(chartType)) return null
  if (!result.data.length || !result.columnList.length) return null
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!measureIdx.length && chartType !== 'table') return null

  const dimCol = dimIdx[0]
  const categories =
    dimCol != null
      ? result.data.map((row) => cell(row, dimCol))
      : result.data.map((_, i) => String(i + 1))

  switch (chartType) {
    case 'boxplot': {
      const mi = measureIdx[0]
      return {
        tooltip: { trigger: 'item' },
        xAxis: { type: 'category', data: categories },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'boxplot',
            data: result.data.map((row) => {
              const v = num(row, mi)
              return [v * 0.7, v * 0.85, v, v * 1.15, v * 1.3]
            }),
          },
        ],
      } as EChartsOption
    }
    case 'line':
    case 'contrast':
    case 'pareto': {
      const st = widgetConfig?.values?.[0]?.series_type
      const seriesKind = chartType === 'contrast' ? { type: 'bar' as const } : lineBarSeriesType(st)
      return {
        tooltip: { trigger: 'axis' },
        legend: measureIdx.length > 1 ? { top: 0 } : undefined,
        grid: { left: 48, right: 24, bottom: 32, top: measureIdx.length > 1 ? 40 : 24 },
        xAxis: { type: 'category', data: categories },
        yAxis: { type: 'value' },
        series: measureIdx.map((mi) => ({
          name: result.columnList[mi].name,
          ...seriesKind,
          data: result.data.map((row) => num(row, mi)),
        })),
      }
    }
    case 'themeRiver': {
      const mi = measureIdx[0]
      if (dimIdx.length >= 2) {
        const xI = dimIdx[0]
        const seriesI = dimIdx[1]
        const xs = [...new Set(result.data.map((r) => cell(r, xI)))]
        const names = [...new Set(result.data.map((r) => cell(r, seriesI)))]
        return {
          tooltip: { trigger: 'axis' },
          legend: { top: 0 },
          xAxis: { type: 'category', data: xs },
          yAxis: { type: 'value' },
          series: names.map((name) => ({
            name,
            type: 'line',
            stack: 'river',
            areaStyle: {},
            data: xs.map((x) => {
              const row = result.data.find((r) => cell(r, xI) === x && cell(r, seriesI) === name)
              return row ? num(row, mi) : 0
            }),
          })),
        } as EChartsOption
      }
      return {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: categories },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'line',
            areaStyle: {},
            stack: 'river',
            data: result.data.map((row) => num(row, mi)),
          },
        ],
      } as EChartsOption
    }
    case 'sankey': {
      if (dimIdx.length < 1 || !measureIdx.length) return null
      const srcI = dimIdx[0]
      const tgtI = dimIdx.length > 1 ? dimIdx[1] : dimIdx[0]
      const mi = measureIdx[0]
      const nodeNames = new Set<string>()
      const links: { source: string; target: string; value: number }[] = []
      for (const row of result.data) {
        const s = cell(row, srcI)
        const t = dimIdx.length > 1 ? cell(row, tgtI) : `${s} (target)`
        if (s === t) continue
        const v = num(row, mi)
        if (v <= 0) continue
        nodeNames.add(s)
        nodeNames.add(t)
        links.push({ source: s, target: t, value: v })
      }
      if (links.length === 0) return null
      return {
        tooltip: { trigger: 'item' },
        series: [
          {
            type: 'sankey',
            data: [...nodeNames].map((name) => ({ name })),
            links,
          },
        ],
      } as EChartsOption
    }
    case 'wordCloud': {
      const mi = measureIdx[0]
      return {
        tooltip: { show: true },
        series: [
          {
            type: 'wordCloud',
            shape: 'circle',
            sizeRange: [12, 48],
            rotationRange: [-45, 45],
            data: result.data.map((row) => ({
              name: dimLabel(row, dimIdx) || 'item',
              value: num(row, mi),
            })),
          },
        ],
      }
    }
    case 'treeMap': {
      if (dimIdx.length < 1) return null
      const mi = measureIdx[0]
      const root: { name: string; value: number; children?: { name: string; value: number }[] }[] = []
      const byKey: Record<string, { name: string; value: number; children: { name: string; value: number }[] }> = {}
      for (const row of result.data) {
        const k0 = cell(row, dimIdx[0])
        const v = num(row, mi)
        if (dimIdx.length === 1) {
          root.push({ name: k0, value: v })
        } else {
          const k1 = cell(row, dimIdx[1])
          if (!byKey[k0]) byKey[k0] = { name: k0, value: 0, children: [] }
          byKey[k0].children.push({ name: k1, value: v })
        }
      }
      const data = [...root, ...Object.values(byKey)]
      return {
        series: [{ type: 'treemap', data }],
      } as EChartsOption
    }
    case 'sunburst': {
      if (dimIdx.length < 1) return null
      const mi = measureIdx[0]
      const tree: Record<string, { name: string; children: { name: string; value: number }[] }> = {}
      for (const row of result.data) {
        const k0 = cell(row, dimIdx[0])
        if (!tree[k0]) tree[k0] = { name: k0, children: [] }
        tree[k0].children.push({
          name: dimIdx.length > 1 ? cell(row, dimIdx[1]) : 'value',
          value: num(row, mi),
        })
      }
      return {
        series: [{ type: 'sunburst', radius: ['15%', '80%'], data: Object.values(tree) }],
      }
    }
    case 'parallel': {
      const axes = [...dimIdx, ...measureIdx]
      if (axes.length < 2) return null
      return {
        parallelAxis: axes.map((colIdx, idx) => ({
          dim: idx,
          name: result.columnList[colIdx].name,
          type: measureIdx.includes(colIdx) ? 'value' : 'category',
        })),
        series: [
          {
            type: 'parallel',
            lineStyle: { width: 1 },
            data: result.data.map((row) =>
              axes.map((i) => (measureIdx.includes(i) ? num(row, i) : cell(row, i))),
            ),
          },
        ],
      } as EChartsOption
    }
    case 'pyramid': {
      const mi = measureIdx[0]
      const data = result.data
        .map((row) => ({ name: dimLabel(row, dimIdx), value: num(row, mi) }))
        .sort((a, b) => b.value - a.value)
      return {
        tooltip: { trigger: 'item' },
        series: [{ type: 'funnel', sort: 'ascending', data }],
      }
    }
    case 'heatMapTable': {
      if (dimIdx.length < 2 || !measureIdx.length) return null
      const xI = dimIdx[0]
      const yI = dimIdx[1]
      const mi = measureIdx[0]
      const xs = [...new Set(result.data.map((r) => cell(r, xI)))]
      const ys = [...new Set(result.data.map((r) => cell(r, yI)))]
      const heatData: [number, number, number][] = []
      for (const row of result.data) {
        heatData.push([xs.indexOf(cell(row, xI)), ys.indexOf(cell(row, yI)), num(row, mi)])
      }
      return {
        tooltip: { position: 'top' },
        xAxis: { type: 'category', data: xs },
        yAxis: { type: 'category', data: ys },
        visualMap: { min: 0, max: Math.max(...heatData.map((d) => d[2]), 1), calculable: true },
        series: [{ type: 'heatmap', data: heatData }],
      }
    }
    case 'pie':
    case 'funnel': {
      const mi = measureIdx[0]
      const data = result.data.map((row) => ({
        name: dimCol != null ? cell(row, dimCol) : 'Total',
        value: num(row, mi),
      }))
      return {
        tooltip: { trigger: 'item' },
        series: [
          {
            type: chartType === 'funnel' ? 'funnel' : 'pie',
            radius: chartType === 'funnel' ? undefined : '55%',
            center: ['50%', '50%'],
            data,
          },
        ],
      }
    }
    case 'scatter': {
      if (measureIdx.length >= 2) {
        const x = measureIdx[0]
        const y = measureIdx[1] ?? measureIdx[0]
        return {
          tooltip: { trigger: 'item' },
          xAxis: { type: 'value' },
          yAxis: { type: 'value' },
          series: [
            {
              type: 'scatter',
              data: result.data.map((row) => [num(row, x), num(row, y)]),
            },
          ],
        }
      }
      const mi = measureIdx[0]
      return {
        tooltip: { trigger: 'item' },
        xAxis: { type: 'category', data: categories },
        yAxis: { type: 'value' },
        series: [{ type: 'scatter', data: result.data.map((row) => num(row, mi)) }],
      }
    }
    case 'kpi': {
      const total = result.data.reduce((s, row) => s + num(row, measureIdx[0]), 0)
      return {
        series: [
          {
            type: 'gauge',
            min: 0,
            max: Math.max(total * 1.2, 1),
            detail: { formatter: '{value}', fontSize: 28 },
            data: [{ value: Math.round(total * 100) / 100, name: result.columnList[measureIdx[0]].name }],
          },
        ],
      }
    }
    case 'gauge': {
      const v = num(result.data[0], measureIdx[0])
      return {
        series: [
          {
            type: 'gauge',
            min: 0,
            max: Math.max(v * 1.5, 100),
            detail: { formatter: '{value}' },
            data: [{ value: v, name: result.columnList[measureIdx[0]].name }],
          },
        ],
      }
    }
    case 'radar': {
      if (!dimIdx.length) return null
      const indicators = categories.map((name) => ({ name, max: 1 }))
      const seriesData = measureIdx.map((mi) => {
        const vals = result.data.map((row) => num(row, mi))
        const max = Math.max(...vals, 1)
        indicators.forEach((ind) => {
          ind.max = Math.max(ind.max, max)
        })
        return { name: result.columnList[mi].name, value: vals }
      })
      return {
        tooltip: {},
        radar: { indicator: indicators },
        series: [{ type: 'radar', data: seriesData }],
      }
    }
    default:
      return null
  }
}

export function chartTypeLabel(chartType: string): string {
  return CHART_TYPES.find((c) => c.value === chartType)?.label ?? chartType
}

/** 레거시 widgetCtrl.js chart_types 와 동일 목록 (UI 피커) */
export const CHART_TYPES = [
  { value: 'table', class: 'cTable', label: 'Table' },
  { value: 'line', class: 'cLine', label: 'Line/Bar' },
  { value: 'contrast', class: 'cContrast', label: 'Contrast' },
  { value: 'scatter', class: 'cScatter', label: 'Scatter' },
  { value: 'pie', class: 'cPie', label: 'Pie' },
  { value: 'kpi', class: 'cKpi', label: 'KPI' },
  { value: 'funnel', class: 'cFunnel', label: 'Funnel' },
  { value: 'sankey', class: 'cSankey', label: 'Sankey' },
  { value: 'radar', class: 'cRadar', label: 'Radar' },
  { value: 'gauge', class: 'cGauge', label: 'Gauge' },
  { value: 'wordCloud', class: 'cWordCloud', label: 'Word Cloud' },
  { value: 'treeMap', class: 'cTreeMap', label: 'Tree Map' },
  { value: 'heatMapCalendar', class: 'cHeatMapCalendar', label: 'Heat Map Calendar' },
  { value: 'heatMapTable', class: 'cHeatMapTable', label: 'Heat Map Table' },
  { value: 'liquidFill', class: 'cLiquidFill', label: 'Liquid Fill' },
  { value: 'relation', class: 'cRelation', label: 'Relation' },
  { value: 'googleMap', class: 'cGoogleMap', label: 'Google Map' },
  { value: 'wordBubble', class: 'cWordBubble', label: 'Word Bubble' },
  { value: 'fusionganttcharts', class: 'cFusionganttcharts', label: 'Gantt' },
  { value: 'boxplot', class: 'cBoxplot', label: 'Boxplot' },
  { value: 'themeRiver', class: 'cThemeRiver', label: 'Theme River' },
  { value: 'sunburst', class: 'cSunburst', label: 'Sunburst' },
  { value: 'parallel', class: 'cParallel', label: 'Parallel' },
  { value: 'pyramid', class: 'cPyramid', label: 'Pyramid' },
  { value: 'pareto', class: 'cPareto', label: 'Pareto' },
] as const
