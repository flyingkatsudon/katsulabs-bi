import { useEffect, useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { apiGet, clearToken } from '../api/client';
import { Header } from '../components/Header';
import { Sidebar } from '../components/Sidebar';

export type User = { userId: string; userName: string; avatar: string };
export type Menu = { menuCode: string; menuName: string };
export type Board = { id: number; name: string; userId: string; categoryId?: number };
export type Category = { categoryId: number; categoryName: string };

export function MainLayout() {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const [user, setUser] = useState<User | null>(null);
  const [menuList, setMenuList] = useState<Menu[]>([]);
  const [boardList, setBoardList] = useState<Board[]>([]);
  const [categoryList, setCategoryList] = useState<Category[]>([]);

  useEffect(() => {
    Promise.all([
      apiGet<User>('/cboard/commons/getUserDetail'),
      apiGet<Menu[]>('/cboard/commons/getMenuList'),
      apiGet<Board[]>('/cboard/dashboard/getBoardList'),
      apiGet<Category[]>('/cboard/dashboard/getCategoryList'),
    ])
      .then(([u, menus, boards, categories]) => {
        setUser(u);
        setMenuList(menus);
        setBoardList(boards);
        setCategoryList(categories);
      })
      .catch(() => navigate('/login'));
  }, [navigate]);

  const logout = () => {
    clearToken();
    navigate('/login');
  };

  if (!user) {
    return (
      <div className="content-wrapper">
        <div className="content">Loading...</div>
      </div>
    );
  }

  return (
    <div className={`wrapper ${collapsed ? 'sidebar-collapse' : ''}`}>
      <Header user={user} onToggle={() => setCollapsed((c) => !c)} onLogout={logout} />
      <Sidebar
        user={user}
        boardList={boardList}
        categoryList={categoryList}
        isShowMenu={(code) => menuList.some((m) => m.menuCode === code)}
      />
      <div className="content-wrapper">
        <Outlet
          context={{
            user,
            boardList,
            categoryList,
            refreshBoards: () =>
              apiGet<Board[]>('/cboard/dashboard/getBoardList').then(setBoardList),
          }}
        />
      </div>
    </div>
  );
}
