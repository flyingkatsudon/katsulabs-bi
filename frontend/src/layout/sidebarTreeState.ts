import { useCallback, useEffect, useState } from 'react'

export type SidebarTreeKeys = {
  dashboard: boolean
  myDashboard: boolean
  config: boolean
}

const DEFAULT_TOP: SidebarTreeKeys = {
  dashboard: true,
  myDashboard: true,
  config: true,
}

/** 사이드바 treeview 접기/펼치기 (AdminLTE menu-open) */
export function useSidebarTreeState(categoryIds: number[]) {
  const [top, setTop] = useState<SidebarTreeKeys>(DEFAULT_TOP)
  const [categories, setCategories] = useState<Record<number, boolean>>({})

  useEffect(() => {
    setCategories((prev) => {
      let changed = false
      const next = { ...prev }
      for (const id of categoryIds) {
        if (next[id] === undefined) {
          next[id] = true
          changed = true
        }
      }
      return changed ? next : prev
    })
  }, [categoryIds])

  const toggleTop = useCallback((key: keyof SidebarTreeKeys) => {
    setTop((prev) => ({ ...prev, [key]: !prev[key] }))
  }, [])

  const toggleCategory = useCallback((id: number) => {
    setCategories((prev) => ({ ...prev, [id]: !prev[id] }))
  }, [])

  return { top, categories, toggleTop, toggleCategory }
}

export function treeviewClass(open: boolean): string {
  return open ? 'treeview menu-open' : 'treeview'
}
