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

const MEASURE_NAME_PATTERN =
  /_(ms|size|count|amount|sales|cost|qty|value|num|total|sum|price|rate|volume|weight)$/i
const DIMENSION_NAME_PATTERN = /(_dt$|_date$|^date_|_at$|_time$|^time_|^year$|^month$|^day$)/i

/** Sample rows에서 Measure 후보인지 추정 (Load Data 자동 분류용) */
export function isLikelyMeasureColumn(column: string, rows: Record<string, unknown>[]): boolean {
  const lower = column.toLowerCase()
  if (DIMENSION_NAME_PATTERN.test(lower) && !MEASURE_NAME_PATTERN.test(column)) {
    return false
  }
  const samples = rows.map((r) => r[column]).filter((v) => v != null && v !== '')
  if (samples.length === 0) {
    return MEASURE_NAME_PATTERN.test(column)
  }
  return samples.every((v) => {
    if (typeof v === 'number' && Number.isFinite(v)) return true
    if (typeof v === 'boolean') return false
    if (typeof v === 'string') {
      const trimmed = v.trim()
      if (trimmed === '') return false
      return !Number.isNaN(Number(trimmed))
    }
    return false
  })
}

/** selects 중 schema에 없는 컬럼을 dimension/measure로 자동 분류 */
export function inferSchemaFromPreview(
  columns: string[],
  rows: Record<string, unknown>[],
  existing: DatasetData['schema'],
): DatasetData['schema'] {
  const dimension = [...existing.dimension]
  const measure = [...existing.measure]
  const scratch: DatasetData = {
    schema: { dimension, measure },
    selects: columns,
    datasource: 0,
    query: { sql: '' },
    filters: [],
    expressions: [],
  }

  for (const col of columns) {
    if (columnInSchema(scratch, col)) continue
    const node = createColumnNode(col)
    if (isLikelyMeasureColumn(col, rows)) {
      measure.push(node)
    } else {
      dimension.push(node)
    }
    scratch.schema = { dimension, measure }
  }

  return { dimension, measure }
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
