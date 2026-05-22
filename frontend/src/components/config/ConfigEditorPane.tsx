import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { ConfigResourceId } from '../../utils/parseConfigResourceId'

export type ConfigLoadIssue = 'invalid_id' | 'not_found' | null

type ConfigEditorPaneProps = {
  loading: boolean
  resource: ConfigResourceId
  loadIssue: ConfigLoadIssue
  resourceLabel: string
  listPath?: string
  idleHint: string
  onBackToList: () => void
  children: ReactNode
}

/** Configuration 우측 편집 영역 — 로드 실패 시 편집기 대신 안내만 표시 */
export function ConfigEditorPane({
  loading,
  resource,
  loadIssue,
  resourceLabel,
  listPath: _listPath,
  idleHint,
  onBackToList,
  children,
}: ConfigEditorPaneProps) {
  if (loading) {
    return (
      <div className="box box-default">
        <div className="box-body text-center" style={{ padding: '48px 16px' }}>
          <p className="text-muted">
            <i className="fa fa-refresh fa-spin" aria-hidden /> 불러오는 중…
          </p>
        </div>
      </div>
    )
  }

  if (loadIssue === 'invalid_id' && resource.kind === 'invalid') {
    return (
      <ConfigResourceErrorBox
        title="잘못된 주소입니다"
        message={`${resourceLabel} ID «${resource.raw}» 형식이 올바르지 않습니다.`}
        listLabel={resourceLabel}
        onBackToList={onBackToList}
      />
    )
  }

  if (loadIssue === 'not_found' && resource.kind === 'edit') {
    return (
      <ConfigResourceErrorBox
        title={`${resourceLabel}을(를) 찾을 수 없습니다`}
        message={`ID ${resource.id}인 ${resourceLabel}이(가) 없거나 삭제되었습니다. 왼쪽 목록에서 항목을 선택하세요.`}
        listLabel={resourceLabel}
        onBackToList={onBackToList}
      />
    )
  }

  if (resource.kind === 'none') {
    return (
      <div className="box box-default">
        <div className="box-body">
          <p className="text-muted" style={{ margin: 0 }}>
            {idleHint}
          </p>
        </div>
      </div>
    )
  }

  return <>{children}</>
}

function ConfigResourceErrorBox({
  title,
  message,
  listLabel,
  onBackToList,
}: {
  title: string
  message: string
  listLabel: string
  onBackToList: () => void
}) {
  return (
    <div className="box box-warning">
      <div className="box-body text-center" style={{ padding: '40px 24px' }}>
        <i
          className="fa fa-exclamation-triangle text-warning"
          style={{ fontSize: 48, marginBottom: 16 }}
          aria-hidden
        />
        <h3 style={{ marginTop: 0 }}>{title}</h3>
        <p className="text-muted" style={{ maxWidth: 420, margin: '0 auto 24px' }}>
          {message}
        </p>
        <p>
          <button type="button" className="btn btn-primary" onClick={onBackToList}>
            {listLabel} 목록으로
          </button>
          <Link to="/" className="btn btn-default" style={{ marginLeft: 8 }}>
            홈으로
          </Link>
        </p>
      </div>
    </div>
  )
}
