import { Component, type ErrorInfo, type ReactNode } from 'react'
import { Link } from 'react-router-dom'

type Props = { children: ReactNode }
type State = { error: Error | null }

/** React + 레거시 DOM 충돌 등 렌더 오류 시 흰 화면 대신 안내 */
export class RouteErrorBoundary extends Component<Props, State> {
  state: State = { error: null }

  static getDerivedStateFromError(error: Error): State {
    return { error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Route render error', error, info.componentStack)
  }

  render() {
    if (this.state.error) {
      return (
        <section className="content">
          <div className="callout callout-danger">
            <h4>화면을 표시할 수 없습니다</h4>
            <p>{this.state.error.message}</p>
            <p>
              <Link to="/boards" className="btn btn-default btn-sm">
                Configuration으로 이동
              </Link>
              <button
                type="button"
                className="btn btn-primary btn-sm"
                style={{ marginLeft: 8 }}
                onClick={() => this.setState({ error: null })}
              >
                다시 시도
              </button>
            </p>
          </div>
        </section>
      )
    }
    return this.props.children
  }
}
