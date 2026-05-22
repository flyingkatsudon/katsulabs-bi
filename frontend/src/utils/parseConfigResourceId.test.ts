import { describe, expect, it } from 'vitest'
import { parseConfigResourceId } from './parseConfigResourceId'

describe('parseConfigResourceId', () => {
  it('parses none, new, edit, invalid', () => {
    expect(parseConfigResourceId(null).kind).toBe('none')
    expect(parseConfigResourceId('new').kind).toBe('new')
    expect(parseConfigResourceId('42')).toEqual({ kind: 'edit', id: 42 })
    expect(parseConfigResourceId('abc').kind).toBe('invalid')
    expect(parseConfigResourceId('2833333').kind).toBe('edit')
  })
})
