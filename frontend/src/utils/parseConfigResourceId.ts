export type ConfigResourceId =
  | { kind: 'none' }
  | { kind: 'new' }
  | { kind: 'edit'; id: number }
  | { kind: 'invalid'; raw: string }

/** Configuration URL `?id=` 파싱 (new / 숫자 ID / 잘못된 값) */
export function parseConfigResourceId(selectedId: string | null): ConfigResourceId {
  if (!selectedId) return { kind: 'none' }
  if (selectedId === 'new') return { kind: 'new' }
  const id = Number(selectedId)
  if (!Number.isInteger(id) || id <= 0) {
    return { kind: 'invalid', raw: selectedId }
  }
  return { kind: 'edit', id }
}
