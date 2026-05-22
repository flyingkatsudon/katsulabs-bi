/** Vite public 정적 자산 베이스 경로 */
export const STATIC_ASSETS_BASE = '/katsulabs-bi'

export function staticAsset(path: string): string {
  const p = path.startsWith('/') ? path : `/${path}`
  return `${STATIC_ASSETS_BASE}${p}`
}
