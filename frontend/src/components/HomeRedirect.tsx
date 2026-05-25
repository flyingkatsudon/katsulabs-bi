import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api } from '../api/client'
import type { BoardSummary } from '../api/types'

type HomeRedirectProps = {
  userId: string
}

/** 로그인 후 기본: My Dashboard 의 Demo Board 보기, 없으면 Configuration */
export function HomeRedirect({ userId }: HomeRedirectProps) {
  const [target, setTarget] = useState<string | null>(null)

  useEffect(() => {
    void (async () => {
      try {
        const boards = await api.get<BoardSummary[]>('/api/v1/boards')
        const mine = boards.filter((b) => b.userId === userId)
        const preferred =
          mine.find((b) => b.name === 'FoodMart Sample Dashboard') ??
          mine.find((b) => b.name === 'Real Estate Sample Dashboard') ??
          mine.find((b) => b.name === 'Economic Indicators Sample Dashboard') ??
          mine.find((b) => b.name === 'Chart Gallery') ??
          mine.find((b) => b.name === 'Demo Board') ??
          mine[0]
        setTarget(preferred ? `/mine/${preferred.id}` : '/boards')
      } catch {
        setTarget('/boards')
      }
    })()
  }, [userId])

  if (!target) {
    return (
      <section className="content">
        <p className="text-muted">
          <i className="fa fa-refresh fa-spin" /> 대시보드 로딩 중…
        </p>
      </section>
    )
  }

  return <Navigate to={target} replace />
}
