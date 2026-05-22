import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api, ApiError } from '../api/client'
import type { BoardSummary } from '../api/types'
import { ContentLoading } from './ContentLoading'

type HomeRedirectProps = {
  userId: string
  onSessionExpired: () => void
}

/** 로그인 후 기본: My Dashboard 의 Demo Board 보기, 없으면 Configuration */
export function HomeRedirect({ userId, onSessionExpired }: HomeRedirectProps) {
  const [target, setTarget] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    void (async () => {
      try {
        const boards = await api.get<BoardSummary[]>('/api/v1/boards')
        if (cancelled) return
        const mine = boards.filter((b) => b.userId === userId)
        const preferred =
          mine.find((b) => b.name === 'FoodMart Sample Dashboard') ??
          mine.find((b) => b.name === 'Real Estate Sample Dashboard') ??
          mine.find((b) => b.name === 'Economic Indicators Sample Dashboard') ??
          mine.find((b) => b.name === 'Chart Gallery') ??
          mine.find((b) => b.name === 'Demo Board') ??
          mine[0]
        setTarget(preferred ? `/mine/${preferred.id}` : '/boards')
      } catch (e) {
        if (cancelled) return
        if (e instanceof ApiError && e.status === 401) {
          onSessionExpired()
          return
        }
        setTarget('/boards')
      }
    })()
    return () => {
      cancelled = true
    }
  }, [userId, onSessionExpired])

  if (!target) {
    return <ContentLoading message="대시보드 로딩 중…" />
  }

  return <Navigate to={target} replace />
}
