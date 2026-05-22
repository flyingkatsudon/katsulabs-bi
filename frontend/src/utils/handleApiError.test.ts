import { describe, expect, it, vi } from 'vitest'
import { ApiError } from '../api/client'
import { handleApiError } from './handleApiError'

describe('handleApiError', () => {
  it('handles 401 with session expired callback', () => {
    const onSessionExpired = vi.fn()
    const handled = handleApiError(new ApiError('로그인이 필요합니다.', 401), {
      onSessionExpired,
    })
    expect(handled).toBe(true)
    expect(onSessionExpired).toHaveBeenCalledOnce()
  })

  it('handles 404 with not found callback', () => {
    const onNotFound = vi.fn()
    const handled = handleApiError(new ApiError('Not Found', 404), {
      onSessionExpired: vi.fn(),
      onNotFound,
    })
    expect(handled).toBe(true)
    expect(onNotFound).toHaveBeenCalledOnce()
  })

  it('sets generic error message', () => {
    const setError = vi.fn()
    const handled = handleApiError(new Error('boom'), {
      onSessionExpired: vi.fn(),
      setError,
    })
    expect(handled).toBe(false)
    expect(setError).toHaveBeenCalledWith('boom')
  })
})
