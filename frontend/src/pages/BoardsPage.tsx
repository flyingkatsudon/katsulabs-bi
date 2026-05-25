import { Link } from 'react-router-dom'
import type { BoardSummary } from '../api/types'

type BoardsPageProps = {
  boards: BoardSummary[]
  loading: boolean
  error: string | null
}

export function BoardsPage({ boards, loading, error }: BoardsPageProps) {
  if (loading) {
    return <p>대시보드 목록을 불러오는 중…</p>
  }
  if (error) {
    return <p className="text-danger">{error}</p>
  }

  return (
    <div className="box box-primary">
      <div className="box-header with-border">
        <h3 className="box-title">대시보드</h3>
        <div className="box-tools pull-right">
          <Link to="/boards/new" className="btn btn-primary btn-sm">
            <i className="fa fa-plus" /> 새 대시보드
          </Link>
        </div>
      </div>
      <div className="box-body table-responsive no-padding">
        <table className="table table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>작성자</th>
              <th>카테고리</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {boards.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.name}</td>
                <td>{b.userName}</td>
                <td>{b.categoryName ?? '-'}</td>
                <td>
                  <Link to={`/boards/${b.id}`} className="btn btn-xs btn-primary">
                    편집
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
