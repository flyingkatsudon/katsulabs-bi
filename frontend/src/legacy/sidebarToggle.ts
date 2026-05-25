/** AdminLTE app.js 없이 사이드바 접기 (body.sidebar-collapse) */
export function toggleSidebar(): void {
  const w = window as Window & { jQuery?: (sel: string) => { toggleClass: (c: string) => void } }
  if (w.jQuery) {
    w.jQuery('body').toggleClass('sidebar-collapse')
    return
  }
  document.body.classList.toggle('sidebar-collapse')
}
