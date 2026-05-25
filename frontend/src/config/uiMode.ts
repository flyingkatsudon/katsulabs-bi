/**
 * true  = Tomcat starter.jsp 전체 iframe (개발·비교용)
 * false = React 구현 + 레거시 CSS/메뉴/상세 UX (기본)
 */
export const USE_LEGACY_IFRAME = import.meta.env.VITE_USE_LEGACY_UI === 'true'
