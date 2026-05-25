import { getChartGuide } from '../../utils/chartGuide'

type ChartConfigGuideProps = {
  chartType: string
  compact?: boolean
}

export function ChartConfigGuide({ chartType, compact }: ChartConfigGuideProps) {
  const guide = getChartGuide(chartType)

  if (compact) {
    return (
      <span
        className="text-muted"
        style={{ marginLeft: 6, cursor: 'help' }}
        title={[guide.summary, ...guide.steps].join('\n')}
      >
        <i className="fa fa-question-circle" aria-hidden />
      </span>
    )
  }

  return (
    <div className="callout callout-info" style={{ marginBottom: 12, padding: '10px 14px' }}>
      <h4 style={{ marginTop: 0, fontSize: 14 }}>
        <i className="fa fa-lightbulb-o" /> 차트 설정 가이드
        <span
          className="label label-default"
          style={{ marginLeft: 8, fontWeight: 'normal' }}
          title="현재 선택한 차트 유형"
        >
          {chartType}
        </span>
      </h4>
      <p style={{ marginBottom: 8 }}>{guide.summary}</p>
      <ol style={{ marginBottom: guide.example ? 8 : 0, paddingLeft: 20 }}>
        {guide.steps.map((step, i) => (
          <li key={i} style={{ marginBottom: 4 }}>
            {step}
          </li>
        ))}
      </ol>
      {guide.example && (
        <p className="text-muted" style={{ marginBottom: 0, fontSize: 12 }}>
          <strong>예:</strong> {guide.example}
        </p>
      )}
    </div>
  )
}
