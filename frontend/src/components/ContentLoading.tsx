/** content-wrapper 안 부트스트랩/리다이렉트 대기 UI (null 렌더 방지) */
export function ContentLoading({ message = '로딩 중…' }: { message?: string }) {
  return (
    <section className="content">
      <p className="text-muted">
        <i className="fa fa-refresh fa-spin" aria-hidden /> {message}
      </p>
    </section>
  )
}
