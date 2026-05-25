import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

/** Tomcat Set-Cookie 를 Vite dev(5173) 에서도 브라우저가 저장하도록 정리 */
function rewriteProxyCookies(proxyRes: import('http').IncomingMessage) {
  const raw = proxyRes.headers['set-cookie']
  if (!raw) return
  const list = Array.isArray(raw) ? raw : [raw]
  proxyRes.headers['set-cookie'] = list.map((cookie) =>
    cookie
      .replace(/;\s*Domain=[^;]*/gi, '')
      .replace(/;\s*Secure/gi, '')
      .replace(/;\s*SameSite=None/gi, '; SameSite=Lax'),
  )
}

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [react()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:8081',
          changeOrigin: true,
          secure: false,
          configure: (proxy) => {
            proxy.on('proxyRes', (proxyRes) => rewriteProxyCookies(proxyRes))
          },
        },
      },
    },
  }
})
