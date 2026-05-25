const LEGACY_BASE = import.meta.env.VITE_LEGACY_CBOARD_BASE as string | undefined

type LegacyEditorLinkProps = {
  hashPath: string
  label?: string
}

/** 레거시 Angular 편집기(starter.jsp) — Tomcat WAR 실행 시 */
export function LegacyEditorLink({ hashPath, label = '클래식 편집기에서 열기' }: LegacyEditorLinkProps) {
  if (!LEGACY_BASE) return null
  const url = `${LEGACY_BASE.replace(/\/$/, '')}/cboard/starter.jsp#${hashPath}`
  return (
    <a href={url} target="_blank" rel="noreferrer" className="btn btn-default btn-sm">
      <i className="fa fa-external-link" /> {label}
    </a>
  )
}

export function LegacyEditorFrame({ hashPath }: { hashPath: string }) {
  if (!LEGACY_BASE) return null
  const src = `${LEGACY_BASE.replace(/\/$/, '')}/cboard/starter.jsp#${hashPath}`
  return (
    <div className="box box-default" style={{ marginTop: 16 }}>
      <div className="box-header with-border">
        <h3 className="box-title">클래식 CBoard 편집기</h3>
        <div className="box-tools pull-right">
          <LegacyEditorLink hashPath={hashPath} label="새 탭" />
        </div>
      </div>
      <div className="box-body" style={{ padding: 0 }}>
        <iframe
          title="CBoard legacy config"
          src={src}
          style={{ width: '100%', height: '72vh', border: 0 }}
        />
      </div>
    </div>
  )
}
