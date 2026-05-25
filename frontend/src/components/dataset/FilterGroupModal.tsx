import { useEffect, useState } from 'react'
import { SimpleModal } from '../common/SimpleModal'
import type { FilterGroupNode } from '../../utils/datasetModel'
import { newNodeId } from '../../utils/datasetModel'

type FilterGroupModalProps = {
  open: boolean
  initial?: FilterGroupNode
  onClose: () => void
  onSave: (group: FilterGroupNode) => void
}

export function FilterGroupModal({ open, initial, onClose, onSave }: FilterGroupModalProps) {
  const [group, setGroup] = useState('')
  const [col, setCol] = useState('')
  const [filterType, setFilterType] = useState('=')
  const [values, setValues] = useState('')

  useEffect(() => {
    if (open) {
      setGroup(initial?.group ?? '')
      const f = initial?.filters?.[0]
      setCol(f?.col ?? '')
      setFilterType(f?.type ?? '=')
      setValues((f?.values ?? []).join(', '))
    }
  }, [open, initial])

  return (
    <SimpleModal
      title={initial ? 'Edit Filter Group' : 'New Filter Group'}
      open={open}
      onClose={onClose}
      okLabel="Save"
      onOk={() => {
        if (!group.trim()) {
          window.alert('Group name is required.')
          return
        }
        const filterValues = values
          .split(',')
          .map((v) => v.trim())
          .filter(Boolean)
        onSave({
          id: initial?.id ?? newNodeId(),
          group: group.trim(),
          filters: col
            ? [{ col, type: filterType, values: filterValues }]
            : (initial?.filters ?? []),
        })
        onClose()
      }}
    >
      <div className="form-group">
        <label>Group name</label>
        <input className="form-control" value={group} onChange={(e) => setGroup(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Column</label>
        <input className="form-control" value={col} onChange={(e) => setCol(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Type</label>
        <select className="form-control" value={filterType} onChange={(e) => setFilterType(e.target.value)}>
          <option value="=">=</option>
          <option value="!=">!=</option>
          <option value=">">&gt;</option>
          <option value="<">&lt;</option>
          <option value="≥">≥</option>
          <option value="≤">≤</option>
        </select>
      </div>
      <div className="form-group">
        <label>Values (comma-separated)</label>
        <input className="form-control" value={values} onChange={(e) => setValues(e.target.value)} />
      </div>
    </SimpleModal>
  )
}
