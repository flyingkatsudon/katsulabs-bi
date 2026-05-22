import type { DatasetData } from '../../utils/datasetModel'
import type { ColumnNode, LevelNode, SchemaNode } from '../../utils/datasetModel'

type WidgetSchemaPanelProps = {
  dataset: DatasetData | null
  onPickDimension: (column: string, alias?: string) => void
  onPickMeasure: (column: string, alias?: string) => void
}

function colLabel(c: ColumnNode) {
  return c.alias ?? c.column
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
                <button type="button" className="btn btn-link btn-xs" onClick={() => onPickMeasure(o.column, o.alias)}>
                  <img src="/katsulabs-bi/imgs/schema/bullet_red.png" alt="" /> {colLabel(o)}
                </button>
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
                  <button type="button" className="btn btn-link btn-xs" onClick={() => onPickDimension(c)}>
                    <img src="/katsulabs-bi/imgs/schema/bullet_red.png" alt="" /> {c}
                  </button>
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
                  <button type="button" className="btn btn-link btn-xs" onClick={() => onPickMeasure(e.alias, e.alias)}>
                    <img src="/katsulabs-bi/imgs/schema/bullet_red.png" alt="" /> {e.alias}
                  </button>
                </li>
              ))}
            </ul>
          </li>
        </ul>
      )}
    </div>
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
      <button type="button" className="btn btn-link btn-xs" onClick={() => onPick(node.column, node.alias)}>
        <img src="/katsulabs-bi/imgs/schema/bullet_blue.png" alt="" /> {colLabel(node)}
      </button>
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
            <button type="button" className="btn btn-link btn-xs" onClick={() => onPick(c.column, c.alias)}>
              <img src="/katsulabs-bi/imgs/schema/bullet_blue.png" alt="" /> {colLabel(c)}
            </button>
          </li>
        ))}
      </ul>
    </li>
  )
}
