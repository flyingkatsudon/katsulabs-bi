/**
 * 레거시 JSP body 클래스와 동일하게 맞춤 (login.jsp / starter.jsp).
 */
export function applyCboardBodyClass(pathname: string): void {
  const body = document.body
  body.classList.remove('login-page', 'skin-blue', 'sidebar-mini', 'sidebar-collapse')

  if (pathname === '/login') {
    body.className = 'hold-transition login-page'
    return
  }

  if (pathname.startsWith('/classic')) {
    body.className = 'hold-transition'
    return
  }

  body.className = 'hold-transition skin-blue sidebar-mini fixed'
}

export function initCboardBodyClass(): void {
  applyCboardBodyClass(window.location.pathname)

  // AdminLTE: hold-transition 제거 (app.js 와 동일 효과, React 진입 후 한 번 더)
  const removeTransition = () => {
    bodyHoldTransitionFix()
  }
  if (document.readyState === 'complete') {
    setTimeout(removeTransition, 100)
  } else {
    window.addEventListener('load', () => setTimeout(removeTransition, 100))
  }
}

function bodyHoldTransitionFix(): void {
  const body = document.body
  if (body.classList.contains('hold-transition')) {
    body.classList.remove('hold-transition')
  }
}
