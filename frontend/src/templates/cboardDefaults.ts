export const DEFAULT_BOARD_LAYOUT = JSON.stringify({ rows: [{ widgets: [] }] }, null, 2)

export const H2_LOCAL_JDBC_CONFIG = JSON.stringify(
  {
    aggregateProvider: true,
    password: '',
    pooled: true,
    driver: 'org.h2.Driver',
    jdbcurl:
      'jdbc:h2:mem:insight-board;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE',
    username: 'sa',
  },
  null,
  2,
)

import { emptyDatasetData, serializeDatasetData } from '../utils/datasetModel'

export function buildDatasetTemplate(datasourceId: number, sql = 'SELECT * FROM sales_fact_sample_flat') {
  return serializeDatasetData(emptyDatasetData(datasourceId, sql))
}

export const DEFAULT_WIDGET_DATA = JSON.stringify(
  {
    config: { chart_type: 'table' },
    datasetId: 1,
    expressions: [],
    filterGroups: [],
  },
  null,
  2,
)
