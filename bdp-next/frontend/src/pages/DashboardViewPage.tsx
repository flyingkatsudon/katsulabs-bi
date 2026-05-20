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

type BoardParam = { config?: string };

export function DashboardViewPage() {
  const { id } = useParams();
  const [board, setBoard] = useState<BoardData | null>(null);
  const [kwdFilter, setKwdFilter] = useState('');

  useEffect(() => {
    if (!id) return;
    Promise.all([
      apiGet<BoardData>(`/cboard/dashboard/getBoardData?id=${id}`),
      apiGet<BoardParam>(`/cboard/dashboard/getBoardParam?boardId=${id}`),
    ]).then(([b, param]) => {
      setBoard(b);
      if (param?.config) {
        try {
          const cfg = JSON.parse(param.config) as { kwd_a?: string };
          if (cfg.kwd_a) setKwdFilter(cfg.kwd_a);
        } catch {
          /* ignore */
        }
      }
    });
  }, [id]);

  async function saveParams() {
    if (!id) return;
    const config = JSON.stringify({ kwd_a: kwdFilter });
    const token = getToken();
    await fetch(`/cboard/dashboard/saveBoardParam?boardId=${id}&config=${encodeURIComponent(config)}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
  }

  if (!board) {
    return <div className="content">Loading...</div>;
  }

  const rows = board.layout?.rows ?? [];
  const showParams = board.layout?.containsParam || rows.some((r) => r.type === 'param');

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
      {showParams && (
        <div className="row">
          <div className="col-md-12">
            <div className="box box-solid">
              <div className="box-body form-inline">
                <label>Keyword (kwd_a)</label>
                <input
                  className="form-control"
                  style={{ marginLeft: 8, marginRight: 8 }}
                  value={kwdFilter}
                  onChange={(e) => setKwdFilter(e.target.value)}
                />
                <button type="button" className="btn btn-primary btn-sm" onClick={saveParams}>
                  Apply filters
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
      {rows.map((row, ri) => {
        if (row.type === 'param') return null;
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
