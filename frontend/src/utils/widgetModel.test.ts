import { describe, expect, it } from 'vitest'
import { toWidgetCol } from './widgetModel'

describe('toWidgetCol', () => {
  it('defaults measure aggregate to sum', () => {
    expect(toWidgetCol({ column: 'store_sales' }, true)).toEqual({
      type: 'column',
      col: 'store_sales',
      alias: undefined,
      aggregate_type: 'sum',
    })
  })

  it('preserves explicit aggregate_type', () => {
    expect(toWidgetCol({ column: 'price', aggregate_type: 'avg' }, true)).toEqual({
      type: 'column',
      col: 'price',
      alias: undefined,
      aggregate_type: 'avg',
    })
  })

  it('omits aggregate_type for dimensions', () => {
    expect(toWidgetCol({ column: 'year' }, false)).toEqual({
      type: 'column',
      col: 'year',
      alias: undefined,
      aggregate_type: undefined,
    })
  })
})
