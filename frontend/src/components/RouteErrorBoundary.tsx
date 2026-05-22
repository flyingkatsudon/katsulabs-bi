import { Component, type ErrorInfo, type ReactNode } from 'react'
import { ResourceErrorView } from './ResourceErrorView'

type Props = {
  children: ReactNode
  onGoHome: () => void
  /** 경로 변경 시 이전 렌더 오류 상태 초기화 */
  resetKey: string
}
type State = { error: Error | null }

/** React 렌더 오류 시 흰 화면 대신 안내 */
export class RouteErrorBoundary extends Component<Props, State> {
  state: State = { error: null }

  static getDerivedStateFromError(error: Error): State {
    return { error }
  }

  componentDidUpdate(prevProps: Props) {
    if (prevProps.resetKey !== this.props.resetKey && this.state.error) {
      this.setState({ error: null })
    }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Route render error', error, info.componentStack)
  }

  render() {
    if (this.state.error) {
      return (
        <ResourceErrorView
          title="화면을 표시할 수 없습니다"
          message="일시적인 오류가 발생했습니다. 홈으로 이동한 뒤 다시 시도해 주세요."
          primaryLabel="홈으로"
          onPrimary={this.props.onGoHome}
        />
      )
    }
    return this.props.children
  }
}
