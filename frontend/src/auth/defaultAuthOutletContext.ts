import type { AuthOutletContext } from './AuthOutletContext'

export const DEFAULT_AUTH_OUTLET_CONTEXT: AuthOutletContext = {
  roleId: '2',
  canWriteDashboard: false,
  canWriteDatasource: false,
  canManageUsers: false,
  bootstrapped: false,
}
