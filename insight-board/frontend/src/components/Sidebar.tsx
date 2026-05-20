import { Link, useLocation } from 'react-router-dom';
import { translate } from '../i18n/en';
import type { Board, Category, User } from '../layouts/MainLayout';

type Props = {
  user: User;
  boardList: Board[];
  categoryList: Category[];
  isShowMenu: (code: string) => boolean;
};

export function Sidebar({ user, boardList, categoryList, isShowMenu }: Props) {
  const location = useLocation();

  const boardsByCategory = (categoryId: number) =>
    boardList.filter((b) => b.categoryId === categoryId);

  const myBoards = boardList.filter((b) => b.userId === user.userId || b.userId === '1');

  const linkClass = (path: string) => (location.pathname.startsWith(path) ? 'active' : '');

  return (
    <aside className="main-sidebar">
      <section className="sidebar">
        <div className="user-panel">
          <div className="pull-left image">
            <img src={user.avatar} className="img-circle" alt="User" />
          </div>
          <div className="pull-left info">
            <p>{user.userId}</p>
          </div>
        </div>

        <ul className="sidebar-menu">
          <li className="header">{translate('SIDEBAR.MENU')}</li>

          {isShowMenu('admin.user') && (
            <li className="treeview">
              <a href="#">
                <i className="fa fa-th" />
                <span>{translate('SIDEBAR.ADMIN_PAGE')}</span>
                <span className="pull-right-container">
                  <i className="fa fa-angle-left pull-right" />
                </span>
              </a>
            </li>
          )}

          {isShowMenu('insightboard.insight') && (
            <li className="treeview">
              <a href="/report/index.html" target="_blank" rel="noreferrer">
                <i className="fa fa-th" />
                <span>{translate('SIDEBAR.INSIGHT_REPORT')}</span>
              </a>
            </li>
          )}

          {isShowMenu('insightboard.g_analysis') && (
            <li className="treeview">
              <a href="/report/ga.html" target="_blank" rel="noreferrer">
                <i className="fa fa-th" />
                <span>{translate('SIDEBAR.G_ANALYSIS')}</span>
              </a>
            </li>
          )}

          {isShowMenu('insightboard.e_analysis') && (
            <li className="treeview">
              <a href="#">
                <i className="fa fa-th" />
                <span>{translate('SIDEBAR.E_ANALYSIS')}</span>
              </a>
            </li>
          )}

          <li className={`treeview ${location.pathname.includes('/dashboard') ? 'active' : ''}`}>
            <a href="#">
              <i className="fa fa-th" />
              <span>{translate('SIDEBAR.C_DASHBOARD')}</span>
              <span className="pull-right-container">
                <i className="fa fa-angle-left pull-right" />
              </span>
            </a>
            <ul className="treeview-menu">
              {categoryList.map((c) => {
                const boards = boardsByCategory(c.categoryId);
                if (boards.length === 0) return null;
                return (
                  <li key={c.categoryId}>
                    <a href="#">
                      <i className="fa fa-folder" />
                      {c.categoryName}
                      <span className="pull-right-container">
                        <i className="fa fa-angle-left pull-right" />
                      </span>
                    </a>
                    <ul className="treeview-menu">
                      {boards.map((b) => (
                        <li key={b.id} className={linkClass(`/dashboard/${b.id}`)}>
                          <Link to={`/dashboard/category/${c.categoryId}/${b.id}`}>
                            <i className="fa fa-dashboard" />
                            {b.name}
                          </Link>
                        </li>
                      ))}
                    </ul>
                  </li>
                );
              })}
            </ul>
          </li>

          <li className={`treeview ${location.pathname.includes('/mine') ? 'active' : ''}`}>
            <a href="#">
              <i className="fa fa-th" />
              <span>{translate('SIDEBAR.MY_DASHBOARD')}</span>
              <span className="pull-right-container">
                <i className="fa fa-angle-left pull-right" />
              </span>
            </a>
            <ul className="treeview-menu">
              {myBoards.map((b) => (
                <li key={b.id} className={linkClass(`/mine/${b.id}`)}>
                  <Link to={`/mine/${b.id}`}>
                    <i className="fa fa-dashboard" />
                    {b.name}
                  </Link>
                </li>
              ))}
            </ul>
          </li>

          {isShowMenu('config') && (
            <li
              className={`treeview ${
                location.pathname.startsWith('/config') ? 'active menu-open' : ''
              }`}
            >
              <a href="#">
                <i className="fa fa-cog" />
                <span>{translate('SIDEBAR.CONFIG')}</span>
                <span className="pull-right-container">
                  <i className="fa fa-angle-left pull-right" />
                </span>
              </a>
              <ul className="treeview-menu" style={{ display: location.pathname.startsWith('/config') ? 'block' : undefined }}>
                {isShowMenu('config.datasource') && (
                  <li className={linkClass('/config/datasource')}>
                    <Link to="/config/datasource">
                      <i className="fa fa-database" />
                      {translate('SIDEBAR.DATA_SOURCE')}
                    </Link>
                  </li>
                )}
                {isShowMenu('config.dataset') && (
                  <li className={linkClass('/config/dataset')}>
                    <Link to="/config/dataset">
                      <i className="fa fa-table" />
                      {translate('SIDEBAR.DATASET')}
                    </Link>
                  </li>
                )}
                {isShowMenu('config.widget') && (
                  <li className={linkClass('/config/widget')}>
                    <Link to="/config/widget">
                      <i className="fa fa-line-chart" />
                      {translate('SIDEBAR.WIDGET')}
                    </Link>
                  </li>
                )}
                {isShowMenu('config.board') && (
                  <li className={linkClass('/config/board')}>
                    <Link to="/config/board">
                      <i className="fa fa-puzzle-piece" />
                      {translate('SIDEBAR.DASHBOARD')}
                    </Link>
                  </li>
                )}
                {isShowMenu('config.category') && (
                  <li className={linkClass('/config/category')}>
                    <Link to="/config/category">
                      <i className="fa fa-folder" />
                      {translate('SIDEBAR.DASHBOARD_CATEGORY')}
                    </Link>
                  </li>
                )}
                {isShowMenu('config.job') && (
                  <li className={linkClass('/config/job')}>
                    <Link to="/config/job">
                      <i className="fa fa-clock-o" />
                      {translate('SIDEBAR.JOB')}
                    </Link>
                  </li>
                )}
              </ul>
            </li>
          )}
        </ul>
      </section>
    </aside>
  );
}
