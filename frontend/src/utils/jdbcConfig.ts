export type JdbcConfigFields = {
  driver: string
  jdbcurl: string
  username: string
  password: string
  pooled: boolean
  aggregateProvider: boolean
}

const DEFAULT_JDBC: JdbcConfigFields = {
  driver: 'org.h2.Driver',
  jdbcurl:
    'jdbc:h2:mem:katsulabs-bi;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE',
  username: 'sa',
  password: '',
  pooled: true,
  aggregateProvider: true,
}

export function parseJdbcConfig(json: string | null | undefined): JdbcConfigFields {
  if (!json) return { ...DEFAULT_JDBC }
  try {
    const o = JSON.parse(json) as Record<string, unknown>
    return {
      driver: String(o.driver ?? DEFAULT_JDBC.driver),
      jdbcurl: String(o.jdbcurl ?? ''),
      username: String(o.username ?? ''),
      password: String(o.password ?? ''),
      pooled: o.pooled !== false && o.pooled !== 'false',
      aggregateProvider: o.aggregateProvider === true || o.aggregateProvider === 'true',
    }
  } catch {
    return { ...DEFAULT_JDBC }
  }
}

export function buildJdbcConfig(fields: JdbcConfigFields): string {
  return JSON.stringify({
    driver: fields.driver,
    jdbcurl: fields.jdbcurl,
    username: fields.username,
    password: fields.password,
    pooled: fields.pooled,
    aggregateProvider: fields.aggregateProvider,
  })
}

export { DEFAULT_JDBC }
