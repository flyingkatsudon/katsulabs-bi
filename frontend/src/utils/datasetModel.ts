export type ColumnNode = {
  id: string
  type: 'column'
  column: string
  alias?: string
}

export type LevelNode = {
  id: string
  type: 'level'
  alias: string
  columns: ColumnNode[]
}

export type SchemaNode = ColumnNode | LevelNode

export type ExpressionNode = {
  id: string
  type: 'exp'
  exp: string
  alias: string
}

export type FilterGroupNode = {
  id: string
  group: string
  filters: { col: string; type: string; values: unknown[] }[]
}

export type DatasetData = {
  schema: { dimension: SchemaNode[]; measure: ColumnNode[] }
  selects: string[]
  datasource: number
  query: { sql: string }
  filters: FilterGroupNode[]
  expressions: ExpressionNode[]
  interval?: string
}

export function newNodeId(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID()
  }
  return `n-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`
}

export function emptyDatasetData(datasourceId: number, sql = 'SELECT 1'): DatasetData {
  return {
    schema: { dimension: [], measure: [] },
    selects: [],
    datasource: datasourceId,
    query: { sql },
    filters: [],
    expressions: [],
    interval: '',
  }
}

export function parseDatasetData(json: string | null | undefined): DatasetData {
  if (!json || json.trim() === '') {
    return emptyDatasetData(1)
  }
  const raw = JSON.parse(json) as Partial<DatasetData>
  return {
    schema: {
      dimension: raw.schema?.dimension ?? [],
      measure: raw.schema?.measure ?? [],
    },
    selects: raw.selects ?? [],
    datasource: raw.datasource ?? 1,
    query: { sql: raw.query?.sql ?? 'SELECT 1' },
    filters: raw.filters ?? [],
    expressions: raw.expressions ?? [],
    interval: raw.interval ?? '',
  }
}

export function serializeDatasetData(data: DatasetData): string {
  return JSON.stringify(data, null, 2)
}

export function columnInSchema(data: DatasetData, column: string): boolean {
  for (const m of data.schema.measure) {
    if (m.column === column) return true
  }
  for (const d of data.schema.dimension) {
    if (d.type === 'column' && d.column === column) return true
    if (d.type === 'level') {
      for (const c of d.columns) {
        if (c.column === column) return true
      }
    }
  }
  return false
}

export function createColumnNode(column: string): ColumnNode {
  return { id: newNodeId(), type: 'column', column }
}

export function splitDisplayName(displayName: string) {
  const idx = displayName.lastIndexOf('/')
  if (idx < 0) {
    return { categoryName: 'Default Category', name: displayName.trim() }
  }
  return {
    categoryName: displayName.substring(0, idx).trim() || 'Default Category',
    name: displayName.substring(idx + 1).trim(),
  }
}
