import { useCallback, useRef, useState } from 'react'
import type { WidgetSummary } from '../../api/types'
import { FREE_LAYOUT_GRID, type FreeLayoutWidget } from '../../utils/boardModel'

const DEFAULT_SPAN = { w: 12, h: 8 }

type FreeLayoutEditorProps = {
  widgets: FreeLayoutWidget[]
  catalog: WidgetSummary[]
  onChange: (widgets: FreeLayoutWidget[]) => void
}

export function FreeLayoutEditor({ widgets, catalog, onChange }: FreeLayoutEditorProps) {
  const panelRef = useRef<HTMLDivElement>(null)
  const [draggingId, setDraggingId] = useState<number | null>(null)

  const snap = useCallback((px: number, size: number) => {
    const cell = size / FREE_LAYOUT_GRID
    return Math.max(0, Math.min(FREE_LAYOUT_GRID - 1, Math.floor(px / cell)))
  }, [])

  function handleDrop(e: React.DragEvent) {
    e.preventDefault()
    const panel = panelRef.current
    if (!panel) return
    const raw = e.dataTransfer.getData('application/x-insightboard-widget')
    if (!raw) return
    const item = JSON.parse(raw) as { id: number; name: string }
    const rect = panel.getBoundingClientRect()
    const x = snap(e.clientX - rect.left, rect.width)
    const y = snap(e.clientY - rect.top, rect.height)
    const slot: FreeLayoutWidget = {
      widgetId: item.id,
      name: item.name,
      x,
      y,
      ex: Math.min(FREE_LAYOUT_GRID, x + DEFAULT_SPAN.w),
      ey: Math.min(FREE_LAYOUT_GRID, y + DEFAULT_SPAN.h),
    }
    onChange([...widgets, slot])
  }

  function moveWidget(id: number, clientX: number, clientY: number) {
    const panel = panelRef.current
    if (!panel) return
    const rect = panel.getBoundingClientRect()
    const x = snap(clientX - rect.left, rect.width)
    const y = snap(clientY - rect.top, rect.height)
    onChange(
      widgets.map((w) => {
        if (w.widgetId !== id) return w
        const wSpan = w.ex - w.x
        const hSpan = w.ey - w.y
        return {
          ...w,
          x,
          y,
          ex: Math.min(FREE_LAYOUT_GRID, x + wSpan),
          ey: Math.min(FREE_LAYOUT_GRID, y + hSpan),
        }
      }),
    )
  }

  function removeWidget(widgetId: number) {
    onChange(widgets.filter((w) => w.widgetId !== widgetId))
  }

  const cellPct = 100 / FREE_LAYOUT_GRID

  return (
    <div className="row">
      <div className="col-md-3">
        <div className="box box-solid">
          <div className="box-header">
            <h3 className="box-title">Widgets</h3>
          </div>
          <div className="box-body" style={{ maxHeight: '60vh', overflow: 'auto' }}>
            <p className="text-muted small">캔버스로 드래그하여 배치</p>
            <ul className="list-unstyled">
              {catalog.map((w) => (
                <li key={w.id} style={{ marginBottom: 6 }}>
                  <span
                    draggable
                    className="label label-primary"
                    style={{ cursor: 'grab', display: 'inline-block' }}
                    onDragStart={(ev) => {
                      ev.dataTransfer.setData(
                        'application/x-insightboard-widget',
                        JSON.stringify({ id: w.id, name: w.name }),
                      )
                    }}
                  >
                    {w.name}
                  </span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
      <div className="col-md-9">
        <div
          ref={panelRef}
          className="layoutPanel box box-body"
          style={{
            position: 'relative',
            minHeight: '65vh',
            background: '#f4f4f4',
            backgroundImage:
              'linear-gradient(#ddd 1px, transparent 1px), linear-gradient(90deg, #ddd 1px, transparent 1px)',
            backgroundSize: `${cellPct}% ${cellPct}%`,
          }}
          onDragOver={(e) => e.preventDefault()}
          onDrop={handleDrop}
        >
          {widgets.map((w) => (
            <div
              key={w.widgetId}
              style={{
                position: 'absolute',
                left: `${(w.x / FREE_LAYOUT_GRID) * 100}%`,
                top: `${(w.y / FREE_LAYOUT_GRID) * 100}%`,
                width: `${((w.ex - w.x) / FREE_LAYOUT_GRID) * 100}%`,
                height: `${((w.ey - w.y) / FREE_LAYOUT_GRID) * 100}%`,
                minHeight: 120,
                padding: 4,
                boxSizing: 'border-box',
              }}
            >
              <div
                className="box box-solid box-primary"
                style={{ height: '100%', margin: 0 }}
                draggable
                onDragStart={() => setDraggingId(w.widgetId)}
                onDragEnd={(e) => {
                  if (draggingId === w.widgetId) moveWidget(w.widgetId, e.clientX, e.clientY)
                  setDraggingId(null)
                }}
              >
                <div className="box-header" style={{ padding: '4px 8px' }}>
                  <h3 className="box-title" style={{ fontSize: 13 }}>
                    {w.name}
                  </h3>
                  <button
                    type="button"
                    className="btn btn-box-tool"
                    onClick={() => removeWidget(w.widgetId)}
                  >
                    <i className="fa fa-times" />
                  </button>
                </div>
                <div className="box-body text-muted" style={{ fontSize: 11 }}>
                  grid ({w.x},{w.y}) – ({w.ex},{w.ey})
                </div>
              </div>
            </div>
          ))}
          {widgets.length === 0 && (
            <p className="text-center text-muted" style={{ paddingTop: '30vh' }}>
              왼쪽 위젯을 이 영역으로 드래그하세요
            </p>
          )}
        </div>
      </div>
    </div>
  )
}
