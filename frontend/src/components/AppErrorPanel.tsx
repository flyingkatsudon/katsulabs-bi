type AppErrorPanelProps = {
  title?: string
  message: string
  actionLabel: string
  onAction: () => void
  variant?: 'danger' | 'warning' | 'info'
}

/** 세션 만료·404·잘못된 경로 등 공통 안내 패널 */
export function AppErrorPanel({
  title = '안내',
  message,
  actionLabel,
  onAction,
  variant = 'danger',
}: AppErrorPanelProps) {
  return (
    <section className="content">
      <div className={`callout callout-${variant}`}>
        <h4>{title}</h4>
        <p>{message}</p>
        <p>
          <button type="button" className="btn btn-primary btn-sm" onClick={onAction}>
            {actionLabel}
          </button>
        </p>
      </div>
    </section>
  )
}
