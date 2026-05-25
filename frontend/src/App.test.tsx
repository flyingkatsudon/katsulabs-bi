import { render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

vi.mock('echarts-wordcloud', () => ({}))

import App from './App'

describe('App', () => {
  it('로그인 화면을 표시한다', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((input: RequestInfo) => {
        const url = typeof input === 'string' ? input : input.url
        if (url.includes('/api/v1/auth/session')) {
          return Promise.resolve({ ok: false, status: 204, text: async () => '' } as Response)
        }
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => ({ status: 'UP', message: 'ok' }),
        } as Response)
      }),
    )
    render(<App />)
    await waitFor(() => {
      expect(screen.getByPlaceholderText('사용자 ID')).toBeInTheDocument()
    })
    expect(screen.getByText(/Katsulabs/)).toBeInTheDocument()
  })
})
