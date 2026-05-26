export const WIDGET_FIELD_DRAG_MIME = 'application/x-katsulabs-widget-field'

export type WidgetSchemaDragPayload = {
  kind: 'dimension' | 'measure'
  column: string
  alias?: string
}

export function widgetSchemaDragPayload(payload: WidgetSchemaDragPayload): string {
  return JSON.stringify(payload)
}

export function parseWidgetSchemaDrag(dataTransfer: DataTransfer): WidgetSchemaDragPayload | null {
  const raw = dataTransfer.getData(WIDGET_FIELD_DRAG_MIME)
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw) as WidgetSchemaDragPayload
    if (!parsed.column?.trim()) return null
    if (parsed.kind !== 'dimension' && parsed.kind !== 'measure') return null
    return parsed
  } catch {
    return null
  }
}
