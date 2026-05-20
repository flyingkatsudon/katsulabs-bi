import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cboardDelete, cboardGet, cboardSave } from '../../api/cboard';
import { ConfigWorkspace, ListItem } from '../../components/ConfigWorkspace';
import { translate } from '../../i18n/en';

type WidgetRef = { id: number; name: string };
type Category = { categoryId: number; categoryName: string };

export function BoardPage() {
  const navigate = useNavigate();
  return (
    <ConfigWorkspace
      titleKey="SIDEBAR.DASHBOARD"
      headerKey="CONFIG.DASHBOARD.DASHBOARD_HEADER"
      icon="fa-puzzle-piece"
      fetchList={() => cboardGet<ListItem[]>('getBoardList')}
      onDelete={(id) => cboardDelete('deleteBoard', id).then(() => undefined)}
      renderForm={(item, mode, onSaved) => (
        <BoardForm item={item} mode={mode} onSaved={onSaved} onOpen={(id) => navigate(`/mine/${id}`)} />
      )}
    />
  );
}

function BoardForm({
  item,
  mode,
  onSaved,
  onOpen,
}: {
  item: ListItem | null;
  mode: 'new' | 'edit' | null;
  onSaved: () => void;
  onOpen: (id: number) => void;
}) {
  const [name, setName] = useState(item?.name ?? '');
  const [categoryId, setCategoryId] = useState<number | ''>((item?.categoryId as number) ?? '');
  const [categories, setCategories] = useState<Category[]>([]);
  const [widgets, setWidgets] = useState<WidgetRef[]>([]);
  const [selectedWidgetIds, setSelectedWidgetIds] = useState<number[]>([]);
  const [dragId, setDragId] = useState<number | null>(null);

  useEffect(() => {
    cboardGet<Category[]>('getCategoryList').then(setCategories);
    cboardGet<WidgetRef[]>('getWidgetList').then((list) =>
      setWidgets(list.map((w) => ({ id: w.id, name: w.name }))),
    );
    if (item?.layout && typeof item.layout === 'object') {
      const layout = item.layout as { rows?: Array<{ widgets?: Array<{ widgetId?: number; widget?: { id: number } }> }> };
      const ids: number[] = [];
      layout.rows?.forEach((row) =>
        row.widgets?.forEach((cell) => {
          const id = cell.widgetId ?? cell.widget?.id;
          if (id) ids.push(id);
        }),
      );
      if (ids.length) setSelectedWidgetIds(ids);
    }
  }, [item]);

  if (!mode) return <p className="text-muted">Select or create a board.</p>;

  function toggleWidget(wid: number, checked: boolean) {
    setSelectedWidgetIds((prev) =>
      checked ? (prev.includes(wid) ? prev : [...prev, wid]) : prev.filter((id) => id !== wid),
    );
  }

  function onDrop(targetId: number) {
    if (dragId == null || dragId === targetId) return;
    setSelectedWidgetIds((prev) => {
      const next = prev.filter((id) => id !== dragId);
      const idx = next.indexOf(targetId);
      next.splice(idx, 0, dragId);
      return next;
    });
    setDragId(null);
  }

  async function save() {
    const ordered = selectedWidgetIds.length > 0 ? selectedWidgetIds : widgets.slice(0, 1).map((w) => w.id);
    const rowWidgets = ordered.map((wid) => {
      const w = widgets.find((x) => x.id === wid);
      return {
        width: ordered.length > 1 ? Math.floor(12 / ordered.length) : 12,
        name: w?.name ?? `Widget ${wid}`,
        show: true,
        loading: false,
        widgetId: wid,
        widget: { id: wid },
      };
    });
    const body = {
      id: item?.id,
      name,
      categoryId: categoryId || categories[0]?.categoryId,
      layout: { type: 'grid', rows: [{ height: 360, widgets: rowWidgets }] },
    };
    await cboardSave(mode === 'new' ? 'saveNewBoard' : 'updateBoard', body);
    onSaved();
  }

  return (
    <div>
      <h3>{translate('SIDEBAR.DASHBOARD')}</h3>
      <div className="form-group">
        <label>Name</label>
        <input className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Category</label>
        <select
          className="form-control"
          value={categoryId}
          onChange={(e) => setCategoryId(Number(e.target.value))}
        >
          {categories.map((c) => (
            <option key={c.categoryId} value={c.categoryId}>
              {c.categoryName}
            </option>
          ))}
        </select>
      </div>
      <div className="row">
        <div className="col-md-6">
          <label>Widget palette</label>
          <ul className="list-group">
            {widgets.map((w) => (
              <li key={w.id} className="list-group-item">
                <label style={{ fontWeight: 'normal', margin: 0 }}>
                  <input
                    type="checkbox"
                    checked={selectedWidgetIds.includes(w.id)}
                    onChange={(e) => toggleWidget(w.id, e.target.checked)}
                  />{' '}
                  {w.name}
                </label>
              </li>
            ))}
          </ul>
        </div>
        <div className="col-md-6">
          <label>Board layout (drag to reorder)</label>
          <ul className="list-group">
            {selectedWidgetIds.map((wid) => {
              const w = widgets.find((x) => x.id === wid);
              return (
                <li
                  key={wid}
                  className="list-group-item"
                  draggable
                  onDragStart={() => setDragId(wid)}
                  onDragOver={(e) => e.preventDefault()}
                  onDrop={() => onDrop(wid)}
                  style={{ cursor: 'move' }}
                >
                  <i className="fa fa-arrows" style={{ marginRight: 8 }} />
                  {w?.name ?? wid}
                </li>
              );
            })}
          </ul>
        </div>
      </div>
      <button type="button" className="btn btn-primary" onClick={save}>
        {translate('COMMON.SAVE')}
      </button>
      {item?.id && (
        <button
          type="button"
          className="btn btn-default"
          style={{ marginLeft: 8 }}
          onClick={() => onOpen(item.id)}
        >
          Open board
        </button>
      )}
    </div>
  );
}
