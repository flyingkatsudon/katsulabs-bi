import { useEffect, useState, type MouseEvent } from 'react'
import { NavLink } from 'react-router-dom'
import { api } from '../api/client'
import type { BoardSummary, CategoryItem, LoginResponse } from '../api/types'
import {
  canAccessConfiguration,
  canManageUsers,
  canWriteDashboard,
  canWriteDatasource,
  isViewer,
} from '../utils/permissions'
import { SIDEBAR } from './cboardLabels'
import { treeviewClass, useSidebarTreeState } from './sidebarTreeState'

/** 펼침 ▼ / 접음 ▶ */
function SidebarTreeChevron({ open }: { open: boolean }) {
  return (
    <span className="pull-right-container">
      <i className={`fa pull-right ${open ? 'fa-angle-down' : 'fa-angle-right'}`} aria-hidden />
    </span>
  )
}

type AppSidebarProps = {
  user: LoginResponse
  dimmed?: boolean
}

const navClass = ({ isActive }: { isActive: boolean }) => (isActive ? 'active' : undefined)

function preventNav(e: MouseEvent) {
  e.preventDefault()
}

function boardsInCategory(boards: BoardSummary[], categoryId: number) {
  return boards.filter((b) => b.categoryId === categoryId)
}

function categoriesWithBoards(categories: CategoryItem[], boards: BoardSummary[]) {
  return categories.filter((c) => boardsInCategory(boards, c.id).length > 0)
}

/** AdminLTE sidebar-menu — 역할별 메뉴 */
export function AppSidebar({ user, dimmed = false }: AppSidebarProps) {
  const [boards, setBoards] = useState<BoardSummary[]>([])
  const [categories, setCategories] = useState<CategoryItem[]>([])
  const viewer = isViewer(user.roleId)
  const showConfig = canAccessConfiguration(user.roleId)
  const showDs = canWriteDatasource(user.roleId)
  const showDashConfig = canWriteDashboard(user.roleId)

  useEffect(() => {
    void (async () => {
      try {
        const [b, c] = await Promise.all([
          api.get<BoardSummary[]>('/api/v1/boards'),
          showConfig ? api.get<CategoryItem[]>('/api/v1/categories') : Promise.resolve([] as CategoryItem[]),
        ])
        setBoards(b)
        setCategories(c)
      } catch {
        /* sidebar 보조 데이터 — 실패해도 기본 메뉴는 동작 */
      }
    })()
  }, [showConfig])

  const treeBoards = viewer ? boards : boards
  const myBoards = boards.filter((b) => b.userId === user.userId)
  const dashCategories = categoriesWithBoards(categories, treeBoards)
  const { top, categories: categoryOpen, toggleTop, toggleCategory } = useSidebarTreeState(
    dashCategories.map((c) => c.id),
  )

  return (
    <aside className="main-sidebar" style={dimmed ? { opacity: 0.85 } : undefined}>
      <section className="sidebar">
        <div className="user-panel">
          <div className="pull-left image">
            <img src="/katsulabs-bi/dist/img/user2-160x160.jpg" className="img-circle" alt="" />
          </div>
          <div className="pull-left info">
            <p>{user.userId}</p>
            <small>{user.roleName}</small>
          </div>
        </div>
        <div className="sidebar-menu-scroll">
          <ul className="sidebar-menu" data-widget="tree">
            <li className="header">{SIDEBAR.MENU}</li>
            <li className={treeviewClass(top.dashboard)}>
              <a
                href="#dashboard"
                onClick={(e) => {
                  preventNav(e)
                  toggleTop('dashboard')
                }}
              >
                <i className="fa fa-th" />
                <span>{viewer ? SIDEBAR.DASHBOARD : SIDEBAR.C_DASHBOARD}</span>
                <SidebarTreeChevron open={top.dashboard} />
              </a>
              <ul className="treeview-menu">
                {dashCategories.length === 0 && treeBoards.length > 0 && (
                  <li>
                    <ul className="treeview-menu" style={{ display: 'block', paddingLeft: 0 }}>
                      {treeBoards.map((b) => (
                        <li key={b.id}>
                          <NavLink to={`/mine/${b.id}`} className={navClass}>
                            <i className="fa fa-dashboard" />
                            <span>{b.name}</span>
                          </NavLink>
                        </li>
                      ))}
                    </ul>
                  </li>
                )}
                {dashCategories.map((c) => (
                  <li key={c.id} className={treeviewClass(categoryOpen[c.id] ?? true)}>
                    <a
                      href={`#cat-${c.id}`}
                      onClick={(e) => {
                        preventNav(e)
                        toggleCategory(c.id)
                      }}
                    >
                      <i className="fa fa-folder" />
                      {c.name}
                      <SidebarTreeChevron open={categoryOpen[c.id] ?? true} />
                    </a>
                    <ul className="treeview-menu">
                      {boardsInCategory(treeBoards, c.id).map((b) => (
                        <li key={b.id}>
                          <NavLink to={`/mine/${b.id}`} className={navClass}>
                            <i className="fa fa-dashboard" />
                            <span>{b.name}</span>
                          </NavLink>
                        </li>
                      ))}
                    </ul>
                  </li>
                ))}
              </ul>
            </li>
            {showDashConfig && (
              <li className={treeviewClass(top.myDashboard)}>
                <a
                  href="#my-dashboard"
                  onClick={(e) => {
                    preventNav(e)
                    toggleTop('myDashboard')
                  }}
                >
                  <i className="fa fa-th" />
                  <span>{SIDEBAR.MY_DASHBOARD}</span>
                  <SidebarTreeChevron open={top.myDashboard} />
                </a>
                <ul className="treeview-menu">
                  {myBoards.map((b) => (
                    <li key={b.id}>
                      <NavLink to={`/mine/${b.id}`} className={navClass}>
                        <i className="fa fa-dashboard" />
                        <span>{b.name}</span>
                      </NavLink>
                    </li>
                  ))}
                </ul>
              </li>
            )}
            {showConfig && (
              <li className={treeviewClass(top.config)}>
                <a
                  href="#config"
                  onClick={(e) => {
                    preventNav(e)
                    toggleTop('config')
                  }}
                >
                  <i className="fa fa-cog" />
                  <span>{SIDEBAR.CONFIG}</span>
                  <SidebarTreeChevron open={top.config} />
                </a>
                <ul className="treeview-menu">
                  {showDs && (
                    <>
                      <li>
                        <NavLink to="/datasources" className={navClass}>
                          <i className="fa fa-database" />
                          <span>{SIDEBAR.DATA_SOURCE}</span>
                        </NavLink>
                      </li>
                      <li>
                        <NavLink to="/datasets" className={navClass}>
                          <i className="fa fa-table" />
                          <span>{SIDEBAR.DATASET}</span>
                        </NavLink>
                      </li>
                    </>
                  )}
                  {showDashConfig && (
                    <>
                      <li>
                        <NavLink to="/widgets" className={navClass}>
                          <i className="fa fa-line-chart" />
                          <span>{SIDEBAR.WIDGET}</span>
                        </NavLink>
                      </li>
                      <li>
                        <NavLink to="/boards" className={navClass} end>
                          <i className="fa fa-puzzle-piece" />
                          <span>{SIDEBAR.DASHBOARD}</span>
                        </NavLink>
                      </li>
                      <li>
                        <NavLink to="/categories" className={navClass}>
                          <i className="fa fa-folder" />
                          <span>{SIDEBAR.DASHBOARD_CATEGORY}</span>
                        </NavLink>
                      </li>
                    </>
                  )}
                  {canManageUsers(user.roleId) && (
                    <li>
                      <NavLink to="/users" className={navClass}>
                        <i className="fa fa-users" />
                        <span>Users</span>
                      </NavLink>
                    </li>
                  )}
                </ul>
              </li>
            )}
          </ul>
        </div>
      </section>
    </aside>
  )
}
