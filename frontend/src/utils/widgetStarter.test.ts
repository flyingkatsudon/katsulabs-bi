import { describe, expect, it } from 'vitest'
import { emptyDatasetData } from './datasetModel'
import { applyWidgetMappingReset, pruneWidgetConfigForChartType } from './widgetStarter'
import { defaultWidgetData } from './widgetModel'

describe('widgetStarter reset', () => {
  const schema = emptyDatasetData(1)
  schema.selects = ['dept_name', 'store_sales']
  schema.schema.dimension = [{ id: 'd1', type: 'column', column: 'dept_name' }]
  schema.schema.measure = [{ id: 'm1', type: 'column', column: 'store_sales' }]

  it('clears hidden zones when chart type changes to kpi', () => {
    const pruned = pruneWidgetConfigForChartType(
      {
        chart_type: 'pareto',
        keys: [{ type: 'column', col: 'dept_name' }],
        groups: [{ type: 'column', col: 'pu_name' }],
        values: [{ cols: [{ type: 'column', col: 'store_sales', aggregate_type: 'sum' }] }],
      },
      'kpi',
    )
    expect(pruned.keys).toEqual([])
    expect(pruned.groups).toEqual([])
    expect(pruned.values?.[0]?.cols).toHaveLength(1)
  })

  it('replaces row/column/value when dataset mapping resets', () => {
    const model = defaultWidgetData(1, 'pareto')
    model.config.keys = [{ type: 'column', col: 'old_row' }]
    model.config.groups = [{ type: 'column', col: 'old_column' }]
    model.config.values = [{ cols: [{ type: 'column', col: 'old_value', aggregate_type: 'count' }] }]

    const reset = applyWidgetMappingReset(model, schema, 'pareto')
    expect(reset.config.keys?.map((c) => c.col)).toEqual(['dept_name'])
    expect(reset.config.groups).toEqual([])
    expect(reset.config.values?.[0]?.cols?.map((c) => c.col)).toEqual(['store_sales'])
  })
})
