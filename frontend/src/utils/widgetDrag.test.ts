import { describe, expect, it } from 'vitest'
import { parseWidgetSchemaDrag, widgetSchemaDragPayload, WIDGET_FIELD_DRAG_MIME } from './widgetDrag'

describe('widgetDrag', () => {
  it('serializes and parses drag payload', () => {
    const payload = { kind: 'measure' as const, column: 'store_sales', alias: 'Sales' }
    const dt = {
      getData: (mime: string) => (mime === WIDGET_FIELD_DRAG_MIME ? widgetSchemaDragPayload(payload) : ''),
    } as DataTransfer
    expect(parseWidgetSchemaDrag(dt)).toEqual(payload)
  })

  it('returns null for invalid payload', () => {
    const dt = {
      getData: () => '{"kind":"bad","column":""}',
    } as unknown as DataTransfer
    expect(parseWidgetSchemaDrag(dt)).toBeNull()
  })
})
