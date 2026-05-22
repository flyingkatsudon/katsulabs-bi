import { useState } from 'react'
import { BRAND_LOGO_HTML, BRAND_ORG, BRAND_APP } from '../brand'
import { toggleSidebar } from '../legacy/sidebarToggle'

type AppHeaderProps = {
  userId: string
  roleName?: string
  onLogout?: () => void
  isRestoringSession?: boolean
}

/** 레거시 view/starter/main-header.html 구조 */
export function AppHeader({ userId, roleName, onLogout, isRestoringSession = false }: AppHeaderProps) {
  const [userMenuOpen, setUserMenuOpen] = useState(false)

  return (
    <header className="main-header">
      <a href="/boards" className="logo">
        <span className="logo-mini">
          <span className="fa fa-home" aria-hidden />
        </span>
        <span
          className="logo-lg"
          dangerouslySetInnerHTML={{ __html: BRAND_LOGO_HTML }}
          aria-label={`${BRAND_ORG} ${BRAND_APP}`}
        />
      </a>
      <nav className="navbar navbar-static-top" role="navigation">
        <button
          type="button"
          className="sidebar-toggle"
          aria-label="Toggle navigation"
          onClick={() => toggleSidebar()}
        >
          <span className="sr-only">Toggle navigation</span>
        </button>
        <div className="navbar-custom-menu">
          <ul className="nav navbar-nav">
            {isRestoringSession && (
              <li>
                <span
                  className="navbar-text text-muted"
                  style={{ marginRight: 16, lineHeight: '50px' }}
                  title="세션 복원 중"
                >
                  <span className="fa fa-refresh fa-spin" aria-hidden /> 새로고침 중입니다
                </span>
              </li>
            )}
            <li className={`dropdown user user-menu${userMenuOpen ? ' open' : ''}`}>
              <button
                type="button"
                className="dropdown-toggle"
                style={{ background: 'none', border: 'none', padding: '15px 12px' }}
                onClick={() => setUserMenuOpen((o) => !o)}
              >
                <img src="/katsulabs-bi/dist/img/user2-160x160.jpg" className="user-image" alt="" />
                <span className="hidden-xs">{userId}</span>
              </button>
              <ul className="dropdown-menu">
                <li className="user-header">
                  <img src="/katsulabs-bi/dist/img/user2-160x160.jpg" className="img-circle" alt="" />
                  <p>
                    {userId}
                    {roleName ? <small> — {roleName}</small> : null}
                  </p>
                </li>
                <li className="user-footer">
                  <div className="pull-right">
                    {onLogout ? (
                      <button type="button" className="btn btn-default btn-flat" onClick={onLogout}>
                        SignOut
                      </button>
                    ) : null}
                  </div>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  )
}
