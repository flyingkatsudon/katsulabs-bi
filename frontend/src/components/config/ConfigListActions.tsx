/** 레거시 config 목록 행의 fa-info / copy / edit / delete 아이콘 */
type ConfigListActionsProps = {
  onInfo?: () => void
  onCopy?: () => void
  onEdit: () => void
  onDelete?: () => void
}

export function ConfigListActions({ onInfo, onCopy, onEdit, onDelete }: ConfigListActionsProps) {
  return (
    <span className="pull-right text-red">
      {onInfo && (
        <i
          className="fa fa-info"
          style={{ cursor: 'pointer' }}
          title="Information"
          onClick={(e) => {
            e.preventDefault()
            e.stopPropagation()
            onInfo()
          }}
        />
      )}
      {onInfo && <>&nbsp;</>}
      {onCopy && (
        <i
          className="fa fa-copy"
          style={{ cursor: 'pointer' }}
          title="Copy"
          onClick={(e) => {
            e.preventDefault()
            e.stopPropagation()
            onCopy()
          }}
        />
      )}
      {onCopy && <>&nbsp;</>}
      <i
        className="fa fa-edit"
        style={{ cursor: 'pointer' }}
        title="Edit"
        onClick={(e) => {
          e.preventDefault()
          e.stopPropagation()
          onEdit()
        }}
      />
      &nbsp;
      {onDelete && (
        <i
          className="fa fa-trash-o"
          style={{ cursor: 'pointer' }}
          title="Delete"
          onClick={(e) => {
            e.preventDefault()
            e.stopPropagation()
            onDelete()
          }}
        />
      )}
    </span>
  )
}
