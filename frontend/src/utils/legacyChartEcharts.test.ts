import { describe, expect, it } from 'vitest'
import { buildEchartsOption } from './chartRender'
import type { AggregateResult } from './aggregateApi'

const sample: AggregateResult = {
  columnList: [
    { name: 'sales_country', index: 0, aggType: null },
    { name: 'store_sales', index: 1, aggType: 'sum' },
  ],
  data: [
    ['USA', '100'],
    ['Canada', '80'],
    ['Mexico', '60'],
  ],
}

describe('legacy chart echarts options', () => {
  it('builds googleMap scatter option', () => {
    const opt = buildEchartsOption('googleMap', sample)
    expect(opt).not.toBeNull()
    expect((opt as { series?: { type: string }[] }).series?.[0]?.type).toBe('scatter')
  })

  it('builds liquidFill option', () => {
    const opt = buildEchartsOption('liquidFill', sample)
    expect(opt).not.toBeNull()
    expect((opt as { series?: { type: string }[] }).series?.[0]?.type).toBe('liquidFill')
  })

  it('builds relation graph option', () => {
    const rel: AggregateResult = {
      columnList: [
        { name: 'from', index: 0, aggType: null },
        { name: 'to', index: 1, aggType: null },
        { name: 'v', index: 2, aggType: 'sum' },
      ],
      data: [
        ['USA', 'M', '10'],
        ['Canada', 'F', '5'],
      ],
    }
    const opt = buildEchartsOption('relation', rel)
    expect((opt as { series?: { type: string }[] }).series?.[0]?.type).toBe('graph')
  })
})
