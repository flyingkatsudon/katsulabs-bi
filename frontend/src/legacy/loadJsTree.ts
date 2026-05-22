let jsTreeLoadPromise: Promise<void> | null = null

const JQUERY_SRC = '/katsulabs-bi/plugins/jQuery/jquery-2.2.3.min.js'
const JSTREE_SRC = '/katsulabs-bi/plugins/jstree.min.js'
const JSTREE_CSS = '/katsulabs-bi/plugins/ngJsTree/style.css'

function loadStylesheet(href: string) {
  if (document.querySelector(`link[href="${href}"]`)) return
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = href
  document.head.appendChild(link)
}

function loadScript(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const existing = document.querySelector(`script[src="${src}"]`) as HTMLScriptElement | null
    if (existing) {
      if (existing.dataset.loaded === 'true') {
        resolve()
        return
      }
      existing.addEventListener('load', () => resolve(), { once: true })
      existing.addEventListener('error', () => reject(new Error(`Failed to load ${src}`)), {
        once: true,
      })
      return
    }
    const script = document.createElement('script')
    script.src = src
    script.async = false
    script.onload = () => {
      script.dataset.loaded = 'true'
      resolve()
    }
    script.onerror = () => reject(new Error(`Failed to load ${src}`))
    document.body.appendChild(script)
  })
}

function assertJsTreeReady(): void {
  const w = window as Window & { jQuery?: { fn?: { jstree?: unknown } } }
  if (!w.jQuery?.fn?.jstree) {
    throw new Error('jsTree plugin not available (jQuery required)')
  }
}

/** Configuration 트리: jQuery → jsTree 순서로 지연 로드 (React SPA는 jQuery 미포함) */
export function ensureJsTreeLoaded(): Promise<void> {
  if (!jsTreeLoadPromise) {
    jsTreeLoadPromise = (async () => {
      loadStylesheet(JSTREE_CSS)
      await loadScript(JQUERY_SRC)
      await loadScript(JSTREE_SRC)
      assertJsTreeReady()
    })()
  }
  return jsTreeLoadPromise
}
