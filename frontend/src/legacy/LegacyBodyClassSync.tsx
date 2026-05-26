import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'
import { applyLegacyBodyClass, initLegacyBodyClass } from './legacyBodyClass'

let initialized = false

export function LegacyBodyClassSync() {
  const { pathname } = useLocation()

  useEffect(() => {
    if (!initialized) {
      initLegacyBodyClass()
      initialized = true
    }
    applyLegacyBodyClass(pathname)
  }, [pathname])

  return null
}
