/** dashboard_role.role_id (V8) */
export const ROLE_SUPER_ADMIN = '1'
export const ROLE_VIEWER = '2'
export const ROLE_MANAGER = '3'

export type AuthUser = {
  userId: string
  userName: string
  roleId: string
  roleName: string
}

export function canManageUsers(roleId: string): boolean {
  return roleId === ROLE_SUPER_ADMIN
}

export function canWriteDatasource(roleId: string): boolean {
  return roleId === ROLE_SUPER_ADMIN
}

export function canWriteDashboard(roleId: string): boolean {
  return roleId === ROLE_SUPER_ADMIN || roleId === ROLE_MANAGER
}

export function isViewer(roleId: string): boolean {
  return roleId === ROLE_VIEWER
}

/** Configuration 메뉴 (DataSource·Widget·Board 편집) — Viewer 제외 */
export function canAccessConfiguration(roleId: string): boolean {
  return canWriteDashboard(roleId) || canWriteDatasource(roleId)
}

export function canPublishBoard(roleId: string): boolean {
  return canWriteDashboard(roleId)
}
