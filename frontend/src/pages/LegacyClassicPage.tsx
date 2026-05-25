import { useEffect, useState } from 'react'
import { isLegacyServerReachable } from '../legacy/legacySession'

type LegacyClassicPageProps = {
  /** ui-router hash, e.g. #/config/widget */
  hash: string
}

/**
 * 레거시 starter.jsp 전체 UI (사이드바·설정·위젯 디자이너 포함).
 * React 는 감싸지 않고 iframe 만 표시해 예전 CBoard 와 동일한 UX.
 */
export function LegacyClassicPage({ hash }: LegacyClassicPageProps) {
  const [reachable, setReachable] = useState<boolean | null>(null)
  const normalized = hash.startsWith('#') ? hash : `#${hash}`
  const src = `/bdp/cboard/starter.jsp${normalized}`

  useEffect(() => {
    void isLegacyServerReachable().then(setReachable)
  }, [])

  if (reachable === false) {
    return (
      <div className="content-wrapper" style={{ marginLeft: 0, padding: 24 }}>
        <div className="alert alert-warning">
          <h4>클래식 CBoard UI를 불러올 수 없습니다</h4>
          <p>
            예전과 동일한 화면은 <strong>레거시 Tomcat WAR</strong> 가 필요합니다. 다른 터미널에서:
          </p>
          <pre style={{ background: '#f4f4f4', padding: 12 }}>
            {`docker compose up -d
mvn clean package tomcat7:run -Denv=docker`}
          </pre>
          <p>
            그다음 <code>npm run dev</code> 를 다시 실행하세요 (Vite가 <code>/bdp</code> → 8080 으로
            프록시합니다).
          </p>
          <p>
            Boot API(8081)만 쓰는 간이 편집 UI가 필요하면{' '}
            <code>VITE_USE_LEGACY_UI=false</code> 로 설정하세요.
          </p>
        </div>
      </div>
    )
  }

  return (
    <iframe
      title="CBoard Classic"
      src={src}
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100%',
        height: '100vh',
        border: 'none',
        zIndex: 1000,
      }}
    />
  )
}
