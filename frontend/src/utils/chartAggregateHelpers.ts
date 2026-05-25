import type { AggregateColumn, AggregateResult } from './aggregateApi'

export function splitColumns(columnList: AggregateColumn[]) {
  const dimIdx: number[] = []
  const measureIdx: number[] = []
  columnList.forEach((c, i) => {
    if (c.aggType) measureIdx.push(i)
    else dimIdx.push(i)
  })
  return { dimIdx, measureIdx }
}

export function cell(row: string[], idx: number): string {
  return row[idx] ?? ''
}

export function num(row: string[], idx: number): number {
  const n = Number(row[idx])
  return Number.isFinite(n) ? n : 0
}

export function dimLabel(row: string[], dimIdx: number[]): string {
  return dimIdx.map((i) => cell(row, i)).join('-')
}

export type AggregateSlice = ReturnType<typeof splitColumns> & { result: AggregateResult }
