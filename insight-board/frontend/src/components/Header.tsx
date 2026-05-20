import { translate } from '../i18n/en';
import type { User } from '../layouts/MainLayout';

type Props = {
  user: User;
  onToggle: () => void;
  onLogout: () => void;
};

export function Header({ user, onToggle, onLogout }: Props) {
  return (
    <header className="main-header">
      <a href="/" className="logo">
        <span className="logo-mini">
          <i className="fa fa-home" />
        </span>
        <span className="logo-lg">
          InsightBoard
        </span>
      </a>
      <nav className="navbar navbar-static-top" role="navigation">
        <a
          href="#"
          className="sidebar-toggle"
          onClick={(e) => {
            e.preventDefault();
            onToggle();
          }}
          role="button"
        >
          <span className="sr-only">{translate('HEADER.TOGGLE_NAVIGATION')}</span>
        </a>
        <div className="navbar-custom-menu">
          <ul className="nav navbar-nav">
            <li className="dropdown user user-menu open">
              <a href="#" className="dropdown-toggle">
                <img src={user.avatar} className="user-image" alt="User" />
                <span className="hidden-xs">{user.userId}</span>
              </a>
              <ul className="dropdown-menu" style={{ display: 'block', position: 'absolute', right: 0 }}>
                <li className="user-header">
                  <img src={user.avatar} className="img-circle" alt="User" />
                  <p>
                    <small>{user.userId}</small>
                  </p>
                </li>
                <li className="user-footer">
                  <div className="pull-left">
                    <a className="btn btn-default btn-flat" href="/login">
                      insight
                    </a>
                  </div>
                  <div className="pull-right">
                    <button type="button" className="btn btn-default btn-flat" onClick={onLogout}>
                      {translate('HEADER.SIGN_OUT')}
                    </button>
                  </div>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  );
}
