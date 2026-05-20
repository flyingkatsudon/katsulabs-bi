import { Navigate, Route, Routes } from 'react-router-dom';
import { getToken } from './api/client';
import { MainLayout } from './layouts/MainLayout';
import { DashboardViewPage } from './pages/DashboardViewPage';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { CategoryPage } from './pages/config/CategoryPage';
import { ListConfigPage } from './pages/config/ListConfigPage';

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
        <Route
          path="config/datasource"
          element={
            <ListConfigPage
              titleKey="SIDEBAR.DATA_SOURCE"
              headerKey="CONFIG.DATA_SOURCE.DATA_SOURCE_HEADER"
              fetchPath="/cboard/dashboard/getDatasourceList"
              icon="fa-database"
            />
          }
        />
        <Route
          path="config/dataset"
          element={
            <ListConfigPage
              titleKey="SIDEBAR.DATASET"
              headerKey="CONFIG.DATASET.DATASET_HEADER"
              fetchPath="/cboard/dashboard/getDatasetList"
              icon="fa-table"
            />
          }
        />
        <Route
          path="config/widget"
          element={
            <ListConfigPage
              titleKey="SIDEBAR.WIDGET"
              headerKey="CONFIG.WIDGET.WIDGET_HEADER"
              fetchPath="/cboard/dashboard/getWidgetList"
              icon="fa-line-chart"
            />
          }
        />
        <Route
          path="config/board"
          element={
            <ListConfigPage
              titleKey="SIDEBAR.DASHBOARD"
              headerKey="CONFIG.DASHBOARD.DASHBOARD_HEADER"
              fetchPath="/cboard/dashboard/getBoardList"
              icon="fa-puzzle-piece"
            />
          }
        />
        <Route path="config/category" element={<CategoryPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
