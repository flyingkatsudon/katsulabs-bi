export type BoardWidgetSlot = {
  widgetId: number
  name: string
  width: number
}

/** 레거시 free layout — 47×47 그리드 좌표 */
export type FreeLayoutWidget = {
  widgetId: number
  name: string
  x: number
  y: number
  ex: number
  ey: number
}

export type BoardParam = {
  col: string
  label?: string
  values?: string[]
  defaultValue?: string
}

export type BoardRow = {
  type: 'widget' | 'param'
  widgets?: BoardWidgetSlot[]
  height?: string
  params?: BoardParam[]
}

export type BoardLayout = {
  type?: 'grid' | 'timeline' | 'free'
  rows?: BoardRow[]
  /** type === 'free' 일 때 */
  widgets?: FreeLayoutWidget[]
}

export const FREE_LAYOUT_GRID = 47

export function parseBoardLayout(json: string | null | undefined): BoardLayout {
  if (!json) return { type: 'grid', rows: [] }
  const o = JSON.parse(json) as BoardLayout
  if (o.type === 'free') {
    if (!o.widgets) o.widgets = []
    return o
  }
  if (!o.rows) o.rows = []
  if (!o.type) o.type = 'grid'
  return o
}

export function serializeBoardLayout(layout: BoardLayout): string {
  return JSON.stringify(layout, null, 2)
}

export function emptyGridLayout(): BoardLayout {
  return { type: 'grid', rows: [{ type: 'widget', widgets: [] }] }
}

export function emptyTimelineLayout(): BoardLayout {
  return {
    type: 'timeline',
    rows: [{ type: 'param', params: [] }],
  }
}

export function emptyFreeLayout(): BoardLayout {
  return { type: 'free', widgets: [] }
}

export function isFreeLayout(layout: BoardLayout): boolean {
  return layout.type === 'free'
}
