import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'
import { applyCboardBodyClass, initCboardBodyClass } from './cboardBodyClass'

let initialized = false

export function CboardBodyClassSync() {
  const { pathname } = useLocation()

  useEffect(() => {
    if (!initialized) {
      initCboardBodyClass()
      initialized = true
    }
    applyCboardBodyClass(pathname)
  }, [pathname])

  return null
}
