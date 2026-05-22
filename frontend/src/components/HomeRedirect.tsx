import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api, ApiError } from '../api/client'
import type { BoardSummary, LoginResponse } from '../api/types'
import { isViewer } from '../utils/permissions'
import { ContentLoading } from './ContentLoading'

type HomeRedirectProps = {
  user: LoginResponse
  onSessionExpired: () => void
}

const ADMIN_HOME_BOARD_NAMES = [
  'FoodMart Sample Dashboard',
  'Real Estate Sample Dashboard',
  'Economic Indicators Sample Dashboard',
  'Chart Gallery',
  'Demo Board',
  'Demo Board Updated',
] as const

function pickAdminHome(boards: BoardSummary[], userId: string): BoardSummary | undefined {
  const mine = boards.filter((b) => b.userId === userId)
  for (const name of ADMIN_HOME_BOARD_NAMES) {
    const hit = mine.find((b) => b.name === name) ?? boards.find((b) => b.name === name)
    if (hit) return hit
  }
  return mine[0] ?? boards[0]
}

function pickViewerHome(boards: BoardSummary[], defaultBoardId?: number | null): BoardSummary | undefined {
  if (defaultBoardId != null) {
    const byDefault = boards.find((b) => b.id === defaultBoardId)
    if (byDefault) return byDefault
  }
  return (
    boards.find((b) => b.name === 'FoodMart Sample Dashboard') ??
    boards.find((b) => b.publishedToViewers) ??
    boards[0]
  )
}

/** 로그인 후 역할별 기본 보드로 이동 */
export function HomeRedirect({ user, onSessionExpired }: HomeRedirectProps) {
  const [target, setTarget] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    void (async () => {
      try {
        const boards = await api.get<BoardSummary[]>('/api/v1/boards')
        if (cancelled) return
        const preferred = isViewer(user.roleId)
          ? pickViewerHome(boards, user.defaultBoardId)
          : pickAdminHome(boards, user.userId)
        setTarget(preferred ? `/mine/${preferred.id}` : '/login')
      } catch (e) {
        if (cancelled) return
        if (e instanceof ApiError && e.status === 401) {
          onSessionExpired()
          return
        }
        setTarget('/login')
      }
    })()
    return () => {
      cancelled = true
    }
  }, [user.userId, user.roleId, user.defaultBoardId, onSessionExpired])

  if (!target) {
    return <ContentLoading message="대시보드 로딩 중…" />
  }

  return <Navigate to={target} replace />
}
