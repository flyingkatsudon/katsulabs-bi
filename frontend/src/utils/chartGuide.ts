import { chartConfigZones } from './chartRender'

export type ChartGuide = {
  summary: string
  steps: string[]
  example?: string
}

const GUIDES: Record<string, ChartGuide> = {
  table: {
    summary: '행·열·값을 자유롭게 조합해 표로 봅니다.',
    steps: [
      'Dataset 스키마에서 Dimension → Row, Measure → Value 클릭',
      'Preview 로 집계 결과 확인',
    ],
  },
  line: {
    summary: '시간·카테고리 축에 따라 추이를 선/막대로 표시합니다.',
    steps: [
      'Row: 연도·월 등 시간/구분 차원 (예: the_year)',
      'Value: sum(store_sales) 등 집계할 측정값',
      'Series type으로 Line / Bar / Stacked 선택',
    ],
    example: 'FoodMart: Row=the_year, Value=sum(store_sales)',
  },
  pie: {
    summary: '한 차원의 비율을 파이로 표시합니다.',
    steps: ['Row: 비율을 나눌 항목 (예: sales_country)', 'Value: sum(매출) 하나'],
    example: 'Row=sales_country, Value=sum(store_sales)',
  },
  kpi: {
    summary: '전체 합계 하나를 게이지/KPI로 표시합니다.',
    steps: ['Row/Column 없음', 'Value: sum(지표) 하나만 추가'],
  },
  scatter: {
    summary: '두 측정값의 상관을 점으로 표시합니다.',
    steps: [
      'Row: 선택(국가별 점을 원하면 sales_country)',
      'Value: X·Y에 쓸 측정값 2개 (예: store_sales, store_cost)',
    ],
  },
  sankey: {
    summary: '흐름(출발→도착)을 링크 두께로 표시합니다.',
    steps: ['Row: 출발 차원 (sales_country)', 'Column: 도착 차원 (gender)', 'Value: sum(금액)'],
  },
  radar: {
    summary: '여러 지표를 한 레이더에 겹쳐 비교합니다.',
    steps: ['Row: 비교 축 (sales_country)', 'Value: 측정값 2개 이상'],
  },
  wordCloud: {
    summary: '텍스트 차원의 빈도/크기를 표시합니다.',
    steps: ['Row: 단어 차원 (sales_country)', 'Value: sum(금액)'],
  },
  treeMap: {
    summary: '계층 면적을 사각형 크기로 표시합니다.',
    steps: ['Row: 상위 (sales_region)', 'Column: 하위 (sales_country)', 'Value: sum(금액)'],
  },
  sunburst: {
    summary: '계층을 동심원으로 표시합니다.',
    steps: ['Row: 상위 지역', 'Column: 하위 국가', 'Value: sum(금액)'],
  },
  heatMapTable: {
    summary: '행×열 교차의 값을 색 농도로 표시합니다.',
    steps: ['Row: the_year', 'Column: sales_country', 'Value: sum(store_sales)'],
  },
  funnel: {
    summary: '단계별 크기를 깔때기형으로 표시합니다.',
    steps: ['Row: 단계 차원', 'Value: sum(금액)'],
  },
  gauge: {
    summary: '단일 지표를 게이지로 표시합니다.',
    steps: ['Value: sum(지표) 하나'],
  },
  contrast: {
    summary: '여러 측정값을 막대로 나란히 비교합니다.',
    steps: ['Row: the_year', 'Value: store_sales, store_cost 등 2개+'],
  },
  boxplot: {
    summary: '분포를 상자그림으로 요약합니다.',
    steps: ['Row: 그룹 차원 (sales_country)', 'Value: sum(금액)'],
  },
  parallel: {
    summary: '여러 차원·지표를 평행 좌표로 표시합니다.',
    steps: ['Row: the_year, sales_country 등 2개+', 'Value: sum(금액)'],
  },
  pyramid: {
    summary: '크기 순 피라미드(깔때기 변형)로 표시합니다.',
    steps: ['Row: 카테고리', 'Value: sum(금액)'],
  },
  pareto: {
    summary: '누적 비중과 막대를 함께 표시합니다.',
    steps: ['Row: the_year', 'Value: sum(store_sales)', 'Series type: Bar 권장'],
  },
  themeRiver: {
    summary: '시간에 따른 여러 시리즈 흐름(스택 영역)입니다.',
    steps: ['Row: the_year', 'Column: sales_country', 'Value: sum(store_sales)'],
  },
  googleMap: {
    summary: '국가·지역명을 경위도로 매핑해 버블 지도처럼 표시합니다 (데모 좌표).',
    steps: ['Row: sales_country 등 지역 차원', 'Value: sum(금액)'],
  },
  map: {
    summary: 'googleMap 과 동일 — 지역명 기준 산점도 지도.',
    steps: ['Row: 지역 차원', 'Value: 집계값 1개'],
  },
  areaMap: {
    summary: '지역별 값을 큰 원 크기로 표시합니다.',
    steps: ['Row: 지역', 'Value: sum(금액)'],
  },
  chinaMap: {
    summary: '중국 지역명이 포함되면 해당 좌표를 우선 사용합니다.',
    steps: ['Row: 성·도시 이름', 'Value: 집계값'],
  },
  liquidFill: {
    summary: '단일 지표 합계를 수면(liquid fill) 게이지로 표시합니다.',
    steps: ['Value: sum(지표) 하나'],
  },
  heatMapCalendar: {
    summary: '날짜(연·월)별 값을 달력 히트맵으로 표시합니다.',
    steps: ['Row: the_year (또는 YYYY-MM-DD)', 'Column: month_of_year (선택)', 'Value: sum(금액)'],
  },
  relation: {
    summary: '관계 그래프(노드·링크)로 연결을 표시합니다.',
    steps: ['Row: 출발 노드', 'Column: 도착 노드', 'Value: 가중치'],
  },
  wordBubble: {
    summary: '단어 크기를 원 크기로 표현합니다 (word cloud 변형).',
    steps: ['Row: 라벨 차원', 'Value: sum(금액)'],
  },
  fusionganttcharts: {
    summary: '작업별 막대 길이로 간트를 근사합니다.',
    steps: ['Row: 작업/카테고리', 'Value: 기간(1개) 또는 시작·종료(2개)'],
  },
}

const DEFAULT_GUIDE: ChartGuide = {
  summary: '차트 유형에 맞게 Row / Column / Value 를 채운 뒤 Preview 를 누르세요.',
  steps: [
    '왼쪽 Dataset 트리에서 필드를 클릭해 Row·Column·Value 에 추가',
    '데이터셋을 바꾸면 기존 필드명이 맞지 않을 수 있으니 다시 지정',
    '오류 시 Query 탭에서 생성 SQL 확인',
  ],
}

export function getChartGuide(chartType: string): ChartGuide {
  return GUIDES[chartType] ?? DEFAULT_GUIDE
}

/** Preview 전 검증 — null 이면 OK, 문자열이면 오류 메시지 */
export function validateWidgetForAggregate(
  chartType: string,
  model: { keys?: { col?: string }[]; groups?: { col?: string }[]; values?: { cols?: { col?: string }[] }[] },
): string | null {
  const zones = chartConfigZones(chartType)
  const keys = (model.keys ?? []).filter((k) => (k.col ?? '').trim())
  const groups = (model.groups ?? []).filter((g) => (g.col ?? '').trim())
  const valueCols = (model.values ?? []).flatMap((b) => b.cols ?? []).filter((c) => (c.col ?? '').trim())

  if (zones.value && valueCols.length === 0) {
    return 'Value 영역에 측정값(Measure)을 하나 이상 추가하세요. Dataset 패널에서 Measure 를 클릭합니다.'
  }
  if (zones.row && keys.length === 0 && !['kpi', 'gauge', 'scatter'].includes(chartType)) {
    if (chartType === 'scatter' && valueCols.length >= 2) return null
    return 'Row 영역에 차원(Dimension)을 하나 이상 추가하세요.'
  }
  if (chartType === 'scatter' && valueCols.length < 2) {
    return 'Scatter 는 Value 에 측정값 2개가 필요합니다 (X·Y).'
  }
  if (chartType === 'sankey' && (keys.length === 0 || groups.length === 0)) {
    return 'Sankey 는 Row(출발)와 Column(도착) 각각 1개 이상 필요합니다.'
  }
  if (chartType === 'heatMapTable' && (keys.length === 0 || groups.length === 0)) {
    return 'Heat Map Table 은 Row·Column 각각 1개 이상 필요합니다.'
  }
  if (chartType === 'radar' && keys.length === 0) {
    return 'Radar 는 Row 에 비교할 차원이 필요합니다.'
  }
  return null
}
