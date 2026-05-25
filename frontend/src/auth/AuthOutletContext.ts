export type AuthOutletContext = {
  roleId: string
  canWriteDashboard: boolean
  canWriteDatasource: boolean
  canManageUsers: boolean
  /** 세션 복원 완료 전 — API 401 시 로그아웃 처리하지 않음 */
  bootstrapped: boolean
}
