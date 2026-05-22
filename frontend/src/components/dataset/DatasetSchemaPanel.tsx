import { useState } from 'react'
import type { ColumnNode, DatasetData, ExpressionNode, FilterGroupNode, LevelNode, SchemaNode } from '../../utils/datasetModel'
import { columnInSchema, createColumnNode, newNodeId } from '../../utils/datasetModel'
import { ExpressionModal } from './ExpressionModal'
import { FilterGroupModal } from './FilterGroupModal'

type DatasetSchemaPanelProps = {
  data: DatasetData
  onChange: (data: DatasetData) => void
}

function columnLabel(c: ColumnNode) {
  return c.alias ? `${c.alias} (${c.column})` : c.column
}

export function DatasetSchemaPanel({ data, onChange }: DatasetSchemaPanelProps) {
  const [dragColumn, setDragColumn] = useState<string | null>(null)
  const [expModal, setExpModal] = useState<ExpressionNode | 'new' | null>(null)
  const [filterModal, setFilterModal] = useState<FilterGroupNode | 'new' | null>(null)

  function update(patch: Partial<DatasetData>) {
    onChange({ ...data, ...patch })
  }

  function updateSchema(schema: DatasetData['schema']) {
    onChange({ ...data, schema })
  }

  function addToDimension(column: string, levelIndex?: number) {
    if (columnInSchema(data, column)) return
    const node = createColumnNode(column)
    if (levelIndex != null) {
      const dimension = data.schema.dimension.map((d, i) => {
        if (i === levelIndex && d.type === 'level') {
          return { ...d, columns: [...d.columns, node] }
        }
        return d
      })
      updateSchema({ ...data.schema, dimension })
      return
    }
    updateSchema({
      ...data.schema,
      dimension: [...data.schema.dimension, node],
    })
  }

  function removeFromDimension(index: number) {
    const dimension = [...data.schema.dimension]
    dimension.splice(index, 1)
    updateSchema({ ...data.schema, dimension })
  }

  function removeFromMeasure(index: number) {
    const measure = [...data.schema.measure]
    measure.splice(index, 1)
    updateSchema({ ...data.schema, measure })
  }

  function moveToMeasure(node: ColumnNode, from: 'dimension' | 'level', dimIndex: number, colIndex?: number) {
    const dimension = data.schema.dimension.map((d, i) => {
      if (from === 'dimension' && i === dimIndex) return null
      if (from === 'level' && i === dimIndex && d.type === 'level' && colIndex != null) {
        return { ...d, columns: d.columns.filter((_, ci) => ci !== colIndex) }
      }
      return d
    }).filter((d): d is SchemaNode => d != null)
    updateSchema({
      dimension,
      measure: [...data.schema.measure, { ...node }],
    })
  }

  function moveToDimension(node: ColumnNode, measureIndex: number) {
    const measure = data.schema.measure.filter((_, i) => i !== measureIndex)
    updateSchema({
      dimension: [...data.schema.dimension, { ...node }],
      measure,
    })
  }

  function editAlias(node: ColumnNode, onDone: () => void) {
    const alias = window.prompt('Alias', node.alias ?? '')
    if (alias === null) return
    node.alias = alias || undefined
    onDone()
  }

  function addHierarchy() {
    const alias = window.prompt('Hierarchy name', 'Hierarchy')
    if (!alias) return
    const level: LevelNode = { id: newNodeId(), type: 'level', alias, columns: [] }
    updateSchema({
      ...data.schema,
      dimension: [...data.schema.dimension, level],
    })
  }

  function saveExpression(node: ExpressionNode) {
    if (expModal && expModal !== 'new' && expModal.id) {
      update({
        expressions: data.expressions.map((e) => (e.id === node.id ? { ...node, id: e.id } : e)),
      })
    } else {
      update({ expressions: [...data.expressions, { ...node, id: newNodeId() }] })
    }
  }

  function saveFilterGroup(group: FilterGroupNode) {
    if (filterModal && filterModal !== 'new' && filterModal.id) {
      update({ filters: data.filters.map((f) => (f.id === group.id ? group : f)) })
    } else {
      update({ filters: [...data.filters, group] })
    }
  }

  return (
    <>
      <div className="row" style={{ marginTop: 12 }}>
        <div className="col-md-6">
          <div
            className="form-control"
            style={{ minHeight: 200, height: 'auto', padding: 0, border: '2px dashed #d2d6de' }}
            onDragOver={(e) => e.preventDefault()}
            onDrop={(e) => {
              e.preventDefault()
              if (dragColumn) addToDimension(dragColumn)
              setDragColumn(null)
            }}
          >
            {data.selects.map((col) => (
              <span
                key={col}
                className={`btn btn-sm ${columnInSchema(data, col) ? 'btn-primary' : 'btn-default'}`}
                style={{ margin: '3px 3px' }}
                draggable
                onDragStart={() => setDragColumn(col)}
                onClick={() => addToDimension(col)}
              >
                {col}
              </span>
            ))}
          </div>
        </div>
        <div className="col-md-6">
          <div className="tree tree-bg-dragin" style={{ maxHeight: 700, overflow: 'auto' }}>
            <DimensionTree
              data={data}
              dragColumn={dragColumn}
              onAddHierarchy={addHierarchy}
              onDropColumn={(levelIndex, col) => addToDimension(col, levelIndex)}
              onEditAlias={(node) => editAlias(node, () => updateSchema({ ...data.schema }))}
              onRemoveDimension={removeFromDimension}
              onRemoveLevelColumn={(levelIndex, colIndex) => {
                const dimension = data.schema.dimension.map((d, i) => {
                  if (i === levelIndex && d.type === 'level') {
                    return { ...d, columns: d.columns.filter((_, ci) => ci !== colIndex) }
                  }
                  return d
                })
                updateSchema({ ...data.schema, dimension })
              }}
              onMoveToMeasure={moveToMeasure}
            />
            <MeasureTree
              data={data}
              onEditAlias={(node) => editAlias(node, () => updateSchema({ ...data.schema }))}
              onRemove={removeFromMeasure}
              onMoveToDimension={moveToDimension}
            />
            <ExpressionTree
              expressions={data.expressions}
              onAdd={() => setExpModal('new')}
              onEdit={(o) => setExpModal(o)}
              onDelete={(i) => {
                const expressions = [...data.expressions]
                expressions.splice(i, 1)
                update({ expressions })
              }}
            />
            <FilterTree
              filters={data.filters}
              onAdd={() => setFilterModal('new')}
              onEdit={(o) => setFilterModal(o)}
              onDelete={(i) => {
                const filters = [...data.filters]
                filters.splice(i, 1)
                update({ filters })
              }}
            />
          </div>
        </div>
      </div>
      <ExpressionModal
        open={expModal != null}
        initial={expModal && expModal !== 'new' ? expModal : undefined}
        onClose={() => setExpModal(null)}
        onSave={saveExpression}
      />
      <FilterGroupModal
        open={filterModal != null}
        initial={filterModal && filterModal !== 'new' ? filterModal : undefined}
        onClose={() => setFilterModal(null)}
        onSave={saveFilterGroup}
      />
    </>
  )
}

function DimensionTree({
  data,
  dragColumn,
  onAddHierarchy,
  onDropColumn,
  onEditAlias,
  onRemoveDimension,
  onRemoveLevelColumn,
  onMoveToMeasure,
}: {
  data: DatasetData
  dragColumn: string | null
  onAddHierarchy: () => void
  onDropColumn: (levelIndex: number, col: string) => void
  onEditAlias: (node: ColumnNode) => void
  onRemoveDimension: (i: number) => void
  onRemoveLevelColumn: (levelIndex: number, colIndex: number) => void
  onMoveToMeasure: (n: ColumnNode, from: 'dimension' | 'level', i: number, ci?: number) => void
}) {
  return (
    <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
      <li className="parent_li">
        <span>
          <img src="/katsulabs-bi/imgs/schema/dimension.gif" alt="" />
          <b> Dimension</b>
        </span>
        <img
          src="/katsulabs-bi/imgs/schema/hierarchy_add.png"
          alt=""
          style={{ cursor: 'pointer', marginLeft: 4 }}
          onClick={onAddHierarchy}
        />
        <ul style={{ paddingLeft: 12, listStyle: 'none' }}>
          {data.schema.dimension.map((o, i) =>
            o.type === 'level' ? (
              <li
                key={o.id}
                onDragOver={(e) => e.preventDefault()}
                onDrop={(e) => {
                  e.preventDefault()
                  if (dragColumn) onDropColumn(i, dragColumn)
                }}
              >
                <span>
                  <i className="fa fa-caret-down" />
                  <img src="/katsulabs-bi/imgs/schema/hierarchy.gif" alt="" /> {o.alias}
                </span>
                <i className="fa fa-trash-o" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onRemoveDimension(i)} />
                <ul style={{ paddingLeft: 12, listStyle: 'none' }}>
                  {o.columns.map((c, ci) => (
                    <li key={c.id}>
                      <img src="/katsulabs-bi/imgs/schema/bullet_blue.png" alt="" /> {columnLabel(c)}
                      <i className="fa fa-edit" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onEditAlias(c)} />
                      <i
                        className="fa fa-trash-o"
                        style={{ cursor: 'pointer', marginLeft: 4 }}
                        onClick={() => onRemoveLevelColumn(i, ci)}
                      />
                      <i
                        className="fa fa-exchange"
                        style={{ cursor: 'pointer', marginLeft: 4 }}
                        onClick={() => onMoveToMeasure(c, 'level', i, ci)}
                      />
                    </li>
                  ))}
                </ul>
              </li>
            ) : (
              <li key={o.id}>
                <img src="/katsulabs-bi/imgs/schema/bullet_blue.png" alt="" /> {columnLabel(o)}
                <i className="fa fa-edit" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onEditAlias(o)} />
                <i className="fa fa-trash-o" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onRemoveDimension(i)} />
                <i
                  className="fa fa-exchange"
                  style={{ cursor: 'pointer', marginLeft: 4 }}
                  onClick={() => onMoveToMeasure(o, 'dimension', i)}
                />
              </li>
            ),
          )}
        </ul>
      </li>
    </ul>
  )
}

function MeasureTree({
  data,
  onEditAlias,
  onRemove,
  onMoveToDimension,
}: {
  data: DatasetData
  onEditAlias: (n: ColumnNode) => void
  onRemove: (i: number) => void
  onMoveToDimension: (n: ColumnNode, i: number) => void
}) {
  return (
    <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
      <li className="parent_li">
        <span>
          <img src="/katsulabs-bi/imgs/schema/measure.gif" alt="" />
          <b> Measure</b>
        </span>
        <ul style={{ paddingLeft: 12, listStyle: 'none' }}>
          {data.schema.measure.map((o, i) => (
            <li key={o.id}>
              <img src="/katsulabs-bi/imgs/schema/bullet_red.png" alt="" /> {columnLabel(o)}
              <i className="fa fa-edit" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onEditAlias(o)} />
              <i className="fa fa-trash-o" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onRemove(i)} />
              <i
                className="fa fa-exchange"
                style={{ cursor: 'pointer', marginLeft: 4 }}
                onClick={() => onMoveToDimension(o, i)}
              />
            </li>
          ))}
        </ul>
      </li>
    </ul>
  )
}

function ExpressionTree({
  expressions,
  onAdd,
  onEdit,
  onDelete,
}: {
  expressions: ExpressionNode[]
  onAdd: () => void
  onEdit: (o: ExpressionNode) => void
  onDelete: (i: number) => void
}) {
  return (
    <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
      <li className="parent_li">
        <span>
          <img src="/katsulabs-bi/imgs/schema/measure.gif" alt="" />
          <b> Custom Expression</b>
        </span>
        <i className="glyphicon glyphicon-plus" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={onAdd} />
        <ul style={{ paddingLeft: 12, listStyle: 'none' }}>
          {expressions.map((o, i) => (
            <li key={o.id}>
              <img src="/katsulabs-bi/imgs/schema/bullet_red.png" alt="" /> {o.alias}
              <i className="fa fa-edit" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onEdit(o)} />
              <i className="fa fa-trash-o" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onDelete(i)} />
            </li>
          ))}
        </ul>
      </li>
    </ul>
  )
}

function FilterTree({
  filters,
  onAdd,
  onEdit,
  onDelete,
}: {
  filters: FilterGroupNode[]
  onAdd: () => void
  onEdit: (o: FilterGroupNode) => void
  onDelete: (i: number) => void
}) {
  return (
    <ul style={{ paddingLeft: 5, listStyle: 'none' }}>
      <li className="parent_li">
        <span>
          <img src="/katsulabs-bi/imgs/schema/filter.png" alt="" />
          <b> Filter Group</b>
        </span>
        <i className="glyphicon glyphicon-plus" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={onAdd} />
        <ul style={{ paddingLeft: 12, listStyle: 'none' }}>
          {filters.map((o, i) => (
            <li key={o.id}>
              <img src="/katsulabs-bi/imgs/schema/bullet_green.png" alt="" /> {o.group}
              <i className="fa fa-edit" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onEdit(o)} />
              <i className="fa fa-trash-o" style={{ cursor: 'pointer', marginLeft: 4 }} onClick={() => onDelete(i)} />
            </li>
          ))}
        </ul>
      </li>
    </ul>
  )
}
