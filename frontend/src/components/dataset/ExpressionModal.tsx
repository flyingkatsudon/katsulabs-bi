import { useEffect, useState } from 'react'
import { SimpleModal } from '../common/SimpleModal'
import type { ExpressionNode } from '../../utils/datasetModel'

type ExpressionModalProps = {
  open: boolean
  initial?: ExpressionNode
  onClose: () => void
  onSave: (exp: ExpressionNode) => void
}

export function ExpressionModal({ open, initial, onClose, onSave }: ExpressionModalProps) {
  const [alias, setAlias] = useState('')
  const [exp, setExp] = useState('sum(column)')

  useEffect(() => {
    if (open) {
      setAlias(initial?.alias ?? '')
      setExp(initial?.exp ?? 'sum(column)')
    }
  }, [open, initial])

  return (
    <SimpleModal
      title={initial ? 'Edit Expression' : 'New Expression'}
      open={open}
      onClose={onClose}
      okLabel="Save"
      onOk={() => {
        if (!alias.trim()) {
          window.alert('Alias is required.')
          return
        }
        onSave({
          id: initial?.id ?? '',
          type: 'exp',
          alias: alias.trim(),
          exp: exp.trim(),
        })
        onClose()
      }}
    >
      <div className="form-group">
        <label>Alias</label>
        <input className="form-control" value={alias} onChange={(e) => setAlias(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Expression</label>
        <textarea className="form-control" rows={4} value={exp} onChange={(e) => setExp(e.target.value)} />
        <p className="help-block">예: sum(store_sales), count(*)</p>
      </div>
    </SimpleModal>
  )
}
