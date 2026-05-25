export type LoginResponse = {
  userId: string
  userName: string
  roleId: string
  roleName: string
  displayName?: string
  loginName?: string
  /** 서버 HttpSession ID — X-Insightboard-Session 헤더용 */
  sessionId?: string
}

export type UserSummary = {
  userId: string
  loginName: string
  displayName: string
  roleId: string
  roleName: string
  userStatus: string
}

export type BoardSummary = {
  id: number
  name: string
  userId: string
  userName: string
  categoryId: number | null
  categoryName: string | null
}

export type BoardDetail = BoardSummary & {
  layoutJson: string | null
}

export type CategoryItem = {
  id: number
  name: string
  userId: string
}

export type DatasourceSummary = {
  id: number
  name: string
  type: string
  userName: string
}

export type DatasourceDetail = DatasourceSummary & {
  configJson: string | null
}

export type DatasetSummary = {
  id: number
  name: string
  userName: string
  categoryName: string | null
  datasourceId?: number | null
}

export type DatasetDetail = DatasetSummary & {
  dataJson: string | null
}

export type WidgetSummary = {
  id: number
  name: string
  userName: string
  categoryName: string | null
}

export type WidgetDetail = WidgetSummary & {
  dataJson: string | null
}

export type ServiceResult = {
  status: string
  message: string
  id?: number
}

export type DatasetPreview = {
  columns: string[]
  rows: Record<string, unknown>[]
}
