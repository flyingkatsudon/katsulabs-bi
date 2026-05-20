import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { apiGet, getToken } from '../api/client';
import { ChartWidget } from '../components/ChartWidget';

type LayoutWidget = {
  width?: number;
  name?: string;
  show?: boolean;
  widget?: {
    id: number;
    name?: string;
    data?: { chart_type?: string; option?: Record<string, unknown> };
  };
};

type LayoutRow = {
  height?: number;
  type?: string;
  widgets?: LayoutWidget[];
};

type BoardData = {
  id: number;
  name: string;
  layout?: { type?: string; rows?: LayoutRow[]; containsParam?: boolean };
};

export function DashboardViewPage() {
  const { id } = useParams();
  const [board, setBoard] = useState<BoardData | null>(null);

  useEffect(() => {
    if (!id) return;
    apiGet<BoardData>(`/cboard/dashboard/getBoardData?id=${id}`).then(setBoard);
  }, [id]);

  if (!board) {
    return <div className="content">Loading...</div>;
  }

  const rows = board.layout?.rows ?? [];

  return (
    <div id="inner-container" className="content">
      <section className="content-header">
        <h1>
          {board.name}
          <button
            type="button"
            className="btn btn-default btn-sm pull-right"
            onClick={async () => {
              const token = getToken();
              const res = await fetch(`/cboard/dashboard/exportBoard?id=${id}`, {
                headers: token ? { Authorization: `Bearer ${token}` } : {},
              });
              const blob = await res.blob();
              const url = URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = 'report.xls';
              a.click();
              URL.revokeObjectURL(url);
            }}
          >
            <i className="fa fa-download" /> Export
          </button>
        </h1>
      </section>
      {rows.map((row, ri) => {
        if (row.type === 'param') {
          return (
            <div key={ri} className="row">
              <div className="col-md-12">
                <p className="text-muted">Board parameters (filter bar) — saved via saveBoardParam in a later iteration.</p>
              </div>
            </div>
          );
        }
        return (
          <div key={ri} className="row" style={{ height: row.height ?? 360 }}>
            {(row.widgets ?? []).map((cell, wi) => {
              if (cell.show === false) return null;
              const wdata = cell.widget?.data;
              const col = cell.width ?? 12;
              return (
                <div key={wi} className={`col-md-${col}`}>
                  <div className="box box-solid" style={{ zIndex: 99 }}>
                    <div className="box-header">
                      <h3 className="box-title">{cell.name ?? cell.widget?.name}</h3>
                      <div className="box-tools pull-right">
                        <button type="button" className="btn btn-box-tool" title="Refresh">
                          <i className="fa fa-refresh" />
                        </button>
                        <button type="button" className="btn btn-box-tool">
                          <i className="fa fa-minus" />
                        </button>
                      </div>
                    </div>
                    <div className="box-body" style={{ padding: '3px 0 3px 13px' }}>
                      {wdata ? (
                        <ChartWidget data={wdata} height={(row.height ?? 360) - 48} />
                      ) : (
                        <p className="text-muted">Widget #{cell.widget?.id}</p>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        );
      })}
    </div>
  );
}
