import { SimpleModal } from './common/SimpleModal'

type SessionExpiredDialogProps = {
  open: boolean
  onConfirm: () => void
}

export function SessionExpiredDialog({ open, onConfirm }: SessionExpiredDialogProps) {
  return (
    <SimpleModal
      title="안내"
      open={open}
      onClose={onConfirm}
      onOk={onConfirm}
      okLabel="확인"
      showCancel={false}
    >
      <p>세션이 만료되었습니다. 로그인 화면으로 이동합니다.</p>
    </SimpleModal>
  )
}
