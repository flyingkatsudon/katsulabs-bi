import { useNavigate } from 'react-router-dom'
import { ResourceErrorView } from '../components/ResourceErrorView'

export function NotFoundPage() {
  const navigate = useNavigate()
  return (
    <ResourceErrorView
      title="존재하지 않는 경로입니다"
      message="요청하신 페이지가 없습니다. 메뉴에서 이동하거나 홈으로 돌아가세요."
      primaryLabel="홈으로"
      onPrimary={() => navigate('/', { replace: true })}
    />
  )
}
