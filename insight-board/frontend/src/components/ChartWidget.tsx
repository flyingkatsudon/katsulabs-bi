import ReactECharts from 'echarts-for-react';

type WidgetData = {
  chart_type?: string;
  option?: Record<string, unknown>;
};

export function ChartWidget({ data, height }: { data: WidgetData; height: number }) {
  const option = data?.option ?? {
    title: { text: 'No data' },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [],
  };

  return (
    <ReactECharts option={option} style={{ height: height - 50, width: '100%' }} opts={{ renderer: 'canvas' }} />
  );
}
