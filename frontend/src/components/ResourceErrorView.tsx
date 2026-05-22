import { Link } from 'react-router-dom'

type ResourceErrorViewProps = {
  title: string
  message: string
  primaryLabel: string
  onPrimary: () => void
  secondaryLabel?: string
  secondaryTo?: string
}

/** 보드 보기 등 단독 화면용 리소스 오류 (편집기·빈 폼 없음) */
export function ResourceErrorView({
  title,
  message,
  primaryLabel,
  onPrimary,
  secondaryLabel = '홈으로',
  secondaryTo = '/',
}: ResourceErrorViewProps) {
  return (
    <section className="content">
      <div className="box box-warning">
        <div className="box-body text-center" style={{ padding: '48px 24px' }}>
          <i
            className="fa fa-exclamation-triangle text-warning"
            style={{ fontSize: 56, marginBottom: 20 }}
            aria-hidden
          />
          <h3 style={{ marginTop: 0 }}>{title}</h3>
          <p className="text-muted" style={{ maxWidth: 480, margin: '0 auto 28px' }}>
            {message}
          </p>
          <p>
            <button type="button" className="btn btn-primary" onClick={onPrimary}>
              {primaryLabel}
            </button>
            <Link to={secondaryTo} className="btn btn-default" style={{ marginLeft: 8 }}>
              {secondaryLabel}
            </Link>
          </p>
        </div>
      </div>
    </section>
  )
}
