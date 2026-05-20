import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { cboardDelete, cboardGet, cboardSave } from '../../api/cboard';
import { ConfigWorkspace, ListItem } from '../../components/ConfigWorkspace';
import { ChartWidget } from '../../components/ChartWidget';
import { translate } from '../../i18n/en';

const CHART_TYPES = ['line', 'bar', 'pie', 'gauge'] as const;

function buildOption(type: string, title: string) {
  if (type === 'pie') {
    return {
      title: { text: title },
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '55%', data: [{ value: 40, name: 'A' }, { value: 32, name: 'B' }] }],
    };
  }
  if (type === 'bar') {
    return {
      title: { text: title },
      xAxis: { type: 'category', data: ['A', 'B', 'C'] },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [12, 20, 8] }],
    };
  }
  return {
    title: { text: title },
    xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed'] },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: [12, 18, 9] }],
  };
}

export function WidgetPage() {
  return (
    <ConfigWorkspace
      titleKey="SIDEBAR.WIDGET"
      headerKey="CONFIG.WIDGET.WIDGET_HEADER"
      icon="fa-line-chart"
      fetchList={() => cboardGet<ListItem[]>('getWidgetList')}
      onDelete={(id) => cboardDelete('deleteWidget', id).then(() => undefined)}
      renderForm={(item, mode, onSaved) => <WidgetForm item={item} mode={mode} onSaved={onSaved} />}
    />
  );
}

function WidgetForm({
  item,
  mode,
  onSaved,
}: {
  item: ListItem | null;
  mode: 'new' | 'edit' | null;
  onSaved: () => void;
}) {
  const initial = (item?.data ?? {}) as { chart_type?: string; option?: Record<string, unknown> };
  const [name, setName] = useState(item?.name ?? '');
  const [categoryName, setCategoryName] = useState((item?.categoryName as string) ?? 'default');
  const [chartType, setChartType] = useState(initial.chart_type ?? 'line');

  const previewOption = useMemo(() => buildOption(chartType, name || 'Preview'), [chartType, name]);

  if (!mode) {
    return (
      <p className="text-muted">
        Select a widget or <Link to="/config/widget?new=1">create new</Link>.
      </p>
    );
  }

  async function save() {
    const data = { chart_type: chartType, option: previewOption };
    await cboardSave(mode === 'new' ? 'saveNewWidget' : 'updateWidget', {
      id: item?.id,
      name,
      categoryName,
      data,
    });
    onSaved();
  }

  return (
    <div className="row">
      <div className="col-md-6">
        <h3>{translate('SIDEBAR.WIDGET')}</h3>
        <div className="form-group">
          <label>Name</label>
          <input className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Category</label>
          <input className="form-control" value={categoryName} onChange={(e) => setCategoryName(e.target.value)} />
        </div>
        <div className="form-group">
          <label>Chart type</label>
          <select className="form-control" value={chartType} onChange={(e) => setChartType(e.target.value)}>
            {CHART_TYPES.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </div>
        <button type="button" className="btn btn-primary" onClick={save}>
          {translate('COMMON.SAVE')}
        </button>
      </div>
      <div className="col-md-6">
        <div className="box box-solid">
          <div className="box-header">
            <h3 className="box-title">Preview</h3>
          </div>
          <div className="box-body">
            <ChartWidget data={{ chart_type: chartType, option: previewOption }} height={320} />
          </div>
        </div>
      </div>
    </div>
  );
}
