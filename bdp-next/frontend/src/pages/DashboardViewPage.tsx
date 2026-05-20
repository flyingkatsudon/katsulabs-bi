import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { apiGet } from '../api/client';
import { ChartWidget } from '../components/ChartWidget';

type LayoutRow = {
  height?: number;
  widgets?: Array<{
    width?: number;
    name?: string;
    show?: boolean;
    widget?: { id: number };
  }>;
};

type BoardData = {
  id: number;
  name: string;
  layout?: { type?: string; rows?: LayoutRow[] };
};

type WidgetItem = {
  id: number;
  name: string;
  data?: { chart_type?: string; option?: Record<string, unknown> };
};

export function DashboardViewPage() {
  const { id } = useParams();
  const [board, setBoard] = useState<BoardData | null>(null);
  const [widgets, setWidgets] = useState<Record<number, WidgetItem>>({});

  useEffect(() => {
    if (!id) return;
    Promise.all([
      apiGet<BoardData>(`/cboard/dashboard/getBoardData?id=${id}`),
      apiGet<WidgetItem[]>('/cboard/dashboard/getWidgetList'),
    ]).then(([b, list]) => {
      setBoard(b);
      const map: Record<number, WidgetItem> = {};
      list.forEach((w) => {
        map[w.id] = w;
      });
      setWidgets(map);
    });
  }, [id]);

  if (!board) {
    return <div className="content">Loading...</div>;
  }

  const rows = board.layout?.rows ?? [];

  return (
    <div id="inner-container" className="content">
      <section className="content-header">
        <h1>{board.name}</h1>
      </section>
      {rows.map((row, ri) => (
        <div key={ri} className="row" style={{ height: row.height ?? 360 }}>
          {(row.widgets ?? []).map((cell, wi) => {
            if (!cell.show) return null;
            const wid = cell.widget?.id;
            const wdata = wid ? widgets[wid]?.data : undefined;
            const col = cell.width ?? 12;
            return (
              <div key={wi} className={`col-md-${col}`}>
                <div className="box box-solid" style={{ zIndex: 99 }}>
                  <div className="box-header">
                    <h3 className="box-title">{cell.name}</h3>
                    <div className="box-tools pull-right">
                      <button type="button" className="btn btn-box-tool">
                        <i className="fa fa-refresh" />
                      </button>
                      <button type="button" className="btn btn-box-tool">
                        <i className="fa fa-minus" />
                      </button>
                    </div>
                  </div>
                  <div className="box-body" style={{ padding: '3px 0 3px 13px' }}>
                    {wdata ? (
                      <ChartWidget data={wdata} height={row.height ?? 360} />
                    ) : (
                      <p className="text-muted">Widget #{wid}</p>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
}
