import { describe, expect, it } from 'vitest'
import {
  inferSchemaFromPreview,
  isLikelyMeasureColumn,
  type DatasetData,
} from './datasetModel'

describe('isLikelyMeasureColumn', () => {
  it('treats numeric sample values as measure', () => {
    const rows = [{ sales: 100, name: 'foo' }]
    expect(isLikelyMeasureColumn('sales', rows)).toBe(true)
    expect(isLikelyMeasureColumn('name', rows)).toBe(false)
  })

  it('treats date-like columns as dimension', () => {
    const rows = [{ create_dt: '2026-04-17 00:00:00' }]
    expect(isLikelyMeasureColumn('create_dt', rows)).toBe(false)
  })

  it('treats *_ms columns as measure even with date-like suffix overlap', () => {
    const rows = [{ response_time_ms: 120 }]
    expect(isLikelyMeasureColumn('response_time_ms', rows)).toBe(true)
  })
})

describe('inferSchemaFromPreview', () => {
  it('classifies unassigned columns and keeps existing schema', () => {
    const existing: DatasetData['schema'] = {
      dimension: [{ id: '1', type: 'column', column: 'user_id' }],
      measure: [],
    }
    const rows = [
      { user_id: 'u1', response_time_ms: 100, api_path: '/x' },
      { user_id: 'u2', response_time_ms: 200, api_path: '/y' },
    ]
    const schema = inferSchemaFromPreview(['user_id', 'response_time_ms', 'api_path'], rows, existing)
    expect(
      schema.dimension.filter((d) => d.type === 'column').map((d) => d.column),
    ).toEqual(['user_id', 'api_path'])
    expect(schema.measure.map((m) => m.column)).toEqual(['response_time_ms'])
  })
})
