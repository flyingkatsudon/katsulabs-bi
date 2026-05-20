import type { ReactNode } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { getToken } from './api/client';
import { MainLayout } from './layouts/MainLayout';
import { DashboardViewPage } from './pages/DashboardViewPage';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { BoardPage } from './pages/config/BoardPage';
import { CategoryPage } from './pages/config/CategoryPage';
import { DatasetPage } from './pages/config/DatasetPage';
import { DatasourcePage } from './pages/config/DatasourcePage';
import { WidgetPage } from './pages/config/WidgetPage';

function RequireAuth({ children }: { children: ReactNode }) {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <MainLayout />
          </RequireAuth>
        }
      >
        <Route index element={<HomePage />} />
        <Route path="dashboard/category/:categoryId/:id" element={<DashboardViewPage />} />
        <Route path="mine/:id" element={<DashboardViewPage />} />
        <Route path="config/datasource" element={<DatasourcePage />} />
        <Route path="config/dataset" element={<DatasetPage />} />
        <Route path="config/widget" element={<WidgetPage />} />
        <Route path="config/board" element={<BoardPage />} />
        <Route path="config/category" element={<CategoryPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
