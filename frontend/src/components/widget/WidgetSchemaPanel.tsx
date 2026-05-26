import type { DatasetData } from '../../utils/datasetModel'
import type { ColumnNode, LevelNode, SchemaNode } from '../../utils/datasetModel'
import { widgetSchemaDragPayload, WIDGET_FIELD_DRAG_MIME, type WidgetSchemaDragPayload } from '../../utils/widgetDrag'

type WidgetSchemaPanelProps = {
  dataset: DatasetData | null
  onPickDimension: (column: string, alias?: string) => void
  onPickMeasure: (column: string, alias?: string) => void
}

function colLabel(c: ColumnNode) {
  return c.alias ?? c.column
}

function startFieldDrag(e: React.DragEvent, payload: WidgetSchemaDragPayload) {
  e.dataTransfer.setData('text/plain', payload.column)
  e.dataTransfer.setData(WIDGET_FIELD_DRAG_MIME, widgetSchemaDragPayload(payload))
  e.dataTransfer.effectAllowed = 'copy'
}

export function WidgetSchemaPanel({ dataset, onPickDimension, onPickMeasure }: WidgetSchemaPanelProps) {
  if (!dataset) {
    return <p className="text-muted">데이터셋을 선택하면 스키마가 표시됩니다.</p>
  }

  return (
    <div
      className="tree tree-bg-dragout"
      style={{
        overflow: 'auto',
        border: 'none',
        minHeight: '40vh',
        maxHeight: '79vh',
        marginBottom: 0,
      }}
    >
      <p className="text-muted" style={{ fontSize: 12, margin: '0 10px 8px' }}>
        필드를 Row / Column / Value 영역으로 드래그하거나, 아래 버튼으로 대상을 고른 뒤 클릭하세요.
      </p>
      <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
        <li className="parent_li">
          <span>
            <img src="/katsulabs-bi/imgs/schema/dimension.gif" alt="" />
            <b> Dimension</b>
          </span>
          <ul style={{ paddingLeft: 8, listStyle: 'none' }}>
            {dataset.schema.dimension.map((o) => (
              <SchemaDimensionNode key={o.type === 'level' ? o.id : o.column} node={o} onPick={onPickDimension} />
            ))}
          </ul>
        </li>
      </ul>
      <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
        <li className="parent_li">
          <span>
            <img src="/katsulabs-bi/imgs/schema/measure.gif" alt="" />
            <b> Measure</b>
          </span>
          <ul style={{ paddingLeft: 8, listStyle: 'none' }}>
            {dataset.schema.measure.map((o) => (
              <li key={o.id}>
                <DraggableFieldButton
                  kind="measure"
                  column={o.column}
                  alias={o.alias}
                  icon="/katsulabs-bi/imgs/schema/bullet_red.png"
                  label={colLabel(o)}
                  onClick={() => onPickMeasure(o.column, o.alias)}
                />
              </li>
            ))}
          </ul>
        </li>
      </ul>
      {dataset.selects.length > 0 && (
        <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
          <li className="parent_li">
            <span>
              <b>Unclassified</b>
            </span>
            <ul style={{ paddingLeft: 8, listStyle: 'none' }}>
              {dataset.selects.map((c) => (
                <li key={c}>
                  <DraggableFieldButton
                    kind="dimension"
                    column={c}
                    icon="/katsulabs-bi/imgs/schema/bullet_red.png"
                    label={c}
                    onClick={() => onPickDimension(c)}
                  />
                </li>
              ))}
            </ul>
          </li>
        </ul>
      )}
      {dataset.expressions.length > 0 && (
        <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
          <li className="parent_li">
            <span>
              <img src="/katsulabs-bi/imgs/schema/measure.gif" alt="" />
              <b> Expression</b>
            </span>
            <ul style={{ paddingLeft: 8, listStyle: 'none' }}>
              {dataset.expressions.map((e) => (
                <li key={e.id}>
                  <DraggableFieldButton
                    kind="measure"
                    column={e.alias}
                    alias={e.alias}
                    icon="/katsulabs-bi/imgs/schema/bullet_red.png"
                    label={e.alias}
                    onClick={() => onPickMeasure(e.alias, e.alias)}
                  />
                </li>
              ))}
            </ul>
          </li>
        </ul>
      )}
    </div>
  )
}

function DraggableFieldButton({
  kind,
  column,
  alias,
  icon,
  label,
  onClick,
}: {
  kind: WidgetSchemaDragPayload['kind']
  column: string
  alias?: string
  icon: string
  label: string
  onClick: () => void
}) {
  return (
    <button
      type="button"
      className="btn btn-link btn-xs"
      draggable
      title="Row / Column / Value 영역으로 드래그"
      onClick={onClick}
      onDragStart={(e) => startFieldDrag(e, { kind, column, alias })}
    >
      <img src={icon} alt="" /> {label}
    </button>
  )
}

function SchemaDimensionNode({
  node,
  onPick,
}: {
  node: SchemaNode
  onPick: (col: string, alias?: string) => void
}) {
  if (node.type === 'level') {
    return <LevelNodeView node={node} onPick={onPick} />
  }
  return (
    <li>
      <DraggableFieldButton
        kind="dimension"
        column={node.column}
        alias={node.alias}
        icon="/katsulabs-bi/imgs/schema/bullet_blue.png"
        label={colLabel(node)}
        onClick={() => onPick(node.column, node.alias)}
      />
    </li>
  )
}

function LevelNodeView({ node, onPick }: { node: LevelNode; onPick: (col: string, alias?: string) => void }) {
  return (
    <li>
      <span>
        <i className="fa fa-caret-down" />
        <img src="/katsulabs-bi/imgs/schema/hierarchy.gif" alt="" /> {node.alias}
      </span>
      <ul style={{ paddingLeft: 8, listStyle: 'none' }}>
        {node.columns.map((c) => (
          <li key={c.id}>
            <DraggableFieldButton
              kind="dimension"
              column={c.column}
              alias={c.alias}
              icon="/katsulabs-bi/imgs/schema/bullet_blue.png"
              label={colLabel(c)}
              onClick={() => onPick(c.column, c.alias)}
            />
          </li>
        ))}
      </ul>
    </li>
  )
}
