import type { EChartsOption } from 'echarts'
import type { AggregateResult } from './aggregateApi'
import { cell, dimLabel, num, splitColumns } from './chartAggregateHelpers'
import { resolveGeoCoord } from './geoCoords'

export const LEGACY_ECHARTS_CHART_TYPES = new Set([
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

export function isLegacyEchartsChartType(chartType: string): boolean {
  return LEGACY_ECHARTS_CHART_TYPES.has(chartType)
}

const MAP_TYPES = new Set([
  'googleMap',
  'map',
  'areaMap',
  'chinaMap',
  'chinaMapBmap',
])

function buildMapScatter(chartType: string, result: AggregateResult): EChartsOption | null {
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!dimIdx.length || !measureIdx.length) return null
  const nameI = dimIdx[0]
  const mi = measureIdx[0]
  const preferChina = chartType === 'chinaMap' || chartType === 'chinaMapBmap'
  const points: { name: string; value: [number, number, number] }[] = []
  for (const row of result.data) {
    const name = cell(row, nameI)
    const coord = resolveGeoCoord(name, preferChina)
    if (!coord) continue
    const v = num(row, mi)
    points.push({ name, value: [...coord, v] })
  }
  if (!points.length) return null
  const maxV = Math.max(...points.map((p) => p.value[2]), 1)
  const isArea = chartType === 'areaMap'
  return {
    title: {
      text: preferChina ? '지역 분포 (좌표 근사)' : '국가·지역 분포 (좌표 근사)',
      left: 'center',
      textStyle: { fontSize: 12, fontWeight: 'normal' },
    },
    tooltip: {
      trigger: 'item',
      formatter: (p: { name?: string; value?: number[] }) => {
        const v = p.value
        if (!v || v.length < 3) return p.name ?? ''
        return `${p.name}<br/>값: ${v[2]}`
      },
    },
    grid: { left: 48, right: 24, top: 36, bottom: 40 },
    xAxis: { type: 'value', name: '경도', min: -180, max: 180, splitLine: { show: false } },
    yAxis: { type: 'value', name: '위도', min: -60, max: 80, splitLine: { show: false } },
    visualMap: {
      min: 0,
      max: maxV,
      left: 'left',
      bottom: 8,
      dimension: 2,
      inRange: { color: ['#50a3ba', '#eac736', '#d94e5d'] },
    },
    series: [
      {
        type: 'scatter',
        data: points,
        symbolSize: (val: number | number[]) => {
          const v = Array.isArray(val) ? (val[2] ?? 0) : 0
          const scale = isArea ? 28 : 12
          return Math.max(8, Math.sqrt(v / maxV) * scale)
        },
        label: { show: points.length <= 12, formatter: '{b}', position: 'right' },
      },
    ],
  } as EChartsOption
}

function buildLiquidFill(result: AggregateResult): EChartsOption | null {
  const { measureIdx } = splitColumns(result.columnList)
  if (!measureIdx.length) return null
  const mi = measureIdx[0]
  const total = result.data.reduce((s, row) => s + num(row, mi), 0)
  const max = Math.max(total * 1.25, 1)
  const pct = Math.min(total / max, 1)
  return {
    series: [
      {
        type: 'liquidFill',
        data: [pct, pct * 0.85, pct * 0.7],
        radius: '70%',
        outline: { show: true },
        label: {
          formatter: () => `${Math.round(total * 100) / 100}`,
          fontSize: 22,
        },
      },
    ],
  } as EChartsOption
}

function buildHeatMapCalendar(result: AggregateResult): EChartsOption | null {
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!dimIdx.length || !measureIdx.length) return null
  const mi = measureIdx[0]
  const calData: [string, number][] = []
  for (const row of result.data) {
    const y = cell(row, dimIdx[0])
    const m = dimIdx.length > 1 ? cell(row, dimIdx[1]) : '6'
    const day = dimIdx.length > 2 ? cell(row, dimIdx[2]) : '15'
    const date =
      /^\d{4}-\d{2}-\d{2}$/.test(y) ? y : `${y.padStart(4, '0').slice(0, 4)}-${String(m).padStart(2, '0').slice(-2)}-${String(day).padStart(2, '0').slice(-2)}`
    calData.push([date, num(row, mi)])
  }
  if (!calData.length) return null
  const years = calData.map((d) => d[0].slice(0, 4))
  const minY = years.reduce((a, b) => (a < b ? a : b))
  const maxY = years.reduce((a, b) => (a > b ? a : b))
  const range = minY === maxY ? minY : `${minY}-${maxY}`
  const maxVal = Math.max(...calData.map((d) => d[1]), 1)
  return {
    tooltip: { position: 'top' },
    visualMap: { min: 0, max: maxVal, calculable: true, orient: 'horizontal', left: 'center', bottom: 0 },
    calendar: { top: 40, left: 30, right: 30, cellSize: ['auto', 18], range, dayLabel: { fontSize: 10 } },
    series: [{ type: 'heatmap', coordinateSystem: 'calendar', data: calData }],
  } as EChartsOption
}

function buildRelation(result: AggregateResult): EChartsOption | null {
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!dimIdx.length || !measureIdx.length) return null
  const srcI = dimIdx[0]
  const tgtI = dimIdx.length > 1 ? dimIdx[1] : dimIdx[0]
  const mi = measureIdx[0]
  const nodes = new Set<string>()
  const links: { source: string; target: string; value: number }[] = []
  for (const row of result.data) {
    const s = cell(row, srcI)
    const t = dimIdx.length > 1 ? cell(row, tgtI) : `${s}_t`
    if (s === t) continue
    const v = num(row, mi)
    if (v <= 0) continue
    nodes.add(s)
    nodes.add(t)
    links.push({ source: s, target: t, value: v })
  }
  if (!links.length) return null
  return {
    tooltip: {},
    series: [
      {
        type: 'graph',
        layout: 'force',
        roam: true,
        label: { show: true, fontSize: 10 },
        force: { repulsion: 120, edgeLength: 80 },
        data: [...nodes].map((name) => ({ name, symbolSize: 28 })),
        links: links.map((l) => ({
          source: l.source,
          target: l.target,
          value: l.value,
          lineStyle: { width: Math.min(8, 1 + Math.log10(l.value + 1)) },
        })),
      },
    ],
  } as EChartsOption
}

function buildWordBubble(result: AggregateResult): EChartsOption | null {
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!dimIdx.length || !measureIdx.length) return null
  const mi = measureIdx[0]
  const data = result.data.map((row) => ({
    name: dimLabel(row, dimIdx) || 'item',
    value: num(row, mi),
  }))
  const maxV = Math.max(...data.map((d) => d.value), 1)
  return {
    tooltip: { trigger: 'item' },
    xAxis: { show: false, min: 0, max: 100 },
    yAxis: { show: false, min: 0, max: 100 },
    series: [
      {
        type: 'scatter',
        data: data.map((d, i) => {
          const angle = (i / Math.max(data.length, 1)) * Math.PI * 2
          const r = 30 + (i % 5) * 8
          return {
            name: d.name,
            value: [50 + Math.cos(angle) * r, 50 + Math.sin(angle) * r, d.value],
          }
        }),
        symbolSize: (val: number[]) => Math.max(12, Math.sqrt((val[2] ?? 0) / maxV) * 56),
        label: { show: true, formatter: (p: { name?: string }) => p.name ?? '', fontSize: 11 },
      },
    ],
  } as EChartsOption
}

function buildGantt(result: AggregateResult): EChartsOption | null {
  const { dimIdx, measureIdx } = splitColumns(result.columnList)
  if (!dimIdx.length || !measureIdx.length) return null
  const labelI = dimIdx[0]
  const tasks = result.data.map((row) => cell(row, labelI))
  if (measureIdx.length >= 2) {
    const startI = measureIdx[0]
    const endI = measureIdx[1]
    const durations = result.data.map((row) => Math.max(0, num(row, endI) - num(row, startI)))
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 120, right: 24, top: 24, bottom: 24 },
      xAxis: { type: 'value', name: '기간(종료−시작)' },
      yAxis: { type: 'category', data: tasks, inverse: true },
      series: [{ type: 'bar', data: durations, barWidth: 14 }],
    } as EChartsOption
  }
  const mi = measureIdx[0]
  const durations = result.data.map((row) => num(row, mi))
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 120, right: 24, top: 24, bottom: 24 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: tasks, inverse: true },
    series: [{ type: 'bar', data: durations, barWidth: 14 }],
  } as EChartsOption
}

/** 레거시 chart_type → ECharts 옵션 (미지원 유형이면 undefined) */
export function buildLegacyEchartsOption(
  chartType: string,
  result: AggregateResult,
): EChartsOption | null | undefined {
  if (!LEGACY_ECHARTS_CHART_TYPES.has(chartType)) return undefined
  if (!result.data.length || !result.columnList.length) return null

  if (MAP_TYPES.has(chartType)) return buildMapScatter(chartType, result)
  switch (chartType) {
    case 'liquidFill':
      return buildLiquidFill(result)
    case 'heatMapCalendar':
      return buildHeatMapCalendar(result)
    case 'relation':
      return buildRelation(result)
    case 'wordBubble':
      return buildWordBubble(result)
    case 'fusionganttcharts':
      return buildGantt(result)
    default:
      return null
  }
}
