import type { ReactNode } from 'react'

type SimpleModalProps = {
  title: string
  open: boolean
  onClose: () => void
  onOk?: () => void
  okLabel?: string
  cancelLabel?: string
  /** false 이면 확인(또는 onOk) 버튼만 표시 */
  showCancel?: boolean
  children: ReactNode
}

/** AdminLTE 스타일 간단 모달 (레거시 uibModal 대체) */
export function SimpleModal({
  title,
  open,
  onClose,
  onOk,
  okLabel = 'OK',
  cancelLabel = '취소',
  showCancel = true,
  children,
}: SimpleModalProps) {
  if (!open) return null
  return (
    <div className="modal fade in" style={{ display: 'block' }}>
      <div className="modal-dialog">
        <div className="modal-content">
          <div className="modal-header">
            <button type="button" className="close" onClick={onClose}>
              <span>&times;</span>
            </button>
            <h4 className="modal-title">{title}</h4>
          </div>
          <div className="modal-body">{children}</div>
          <div className="modal-footer">
            {showCancel && (
              <button type="button" className="btn btn-default" onClick={onClose}>
                {cancelLabel}
              </button>
            )}
            {onOk && (
              <button type="button" className="btn btn-primary" onClick={onOk}>
                {okLabel}
              </button>
            )}
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade in" onClick={onClose} />
    </div>
  )
}
