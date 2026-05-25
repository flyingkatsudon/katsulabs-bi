/** 레거시 WAR 세션 (POST /bdp/process) — 클래식 UI iframe 과 동일 쿠키 공유용 */

const BUSINESS_CODE = import.meta.env.VITE_LEGACY_BUSINESS_CODE ?? 'SH'

export async function syncLegacySession(userId: string, password: string): Promise<void> {
  const body = new URLSearchParams()
  body.set('v0', BUSINESS_CODE)
  body.set('v1', userId)
  body.set('v2', password)

  const res = await fetch('/bdp/process', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
    credentials: 'include',
    redirect: 'manual',
  })

  // 302 → starter / 200 → 로그인 실패 페이지
  if (res.type === 'opaqueredirect' || res.status === 302 || res.status === 0) {
    return
  }
  if (!res.ok && res.status !== 302) {
    throw new Error(
      `레거시 로그인 실패 (HTTP ${res.status}). Tomcat WAR(8080) 실행 및 Vite /bdp 프록시를 확인하세요.`,
    )
  }
}

export async function isLegacyServerReachable(): Promise<boolean> {
  try {
    const res = await fetch('/bdp/login.jsp', { method: 'HEAD', credentials: 'include' })
    return res.ok
  } catch {
    return false
  }
}
