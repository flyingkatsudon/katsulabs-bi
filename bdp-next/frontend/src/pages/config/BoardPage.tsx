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

  useEffect(() => {
    cboardGet<Category[]>('getCategoryList').then(setCategories);
    cboardGet<WidgetRef[]>('getWidgetList').then((list) =>
      setWidgets(list.map((w) => ({ id: w.id, name: w.name }))),
    );
  }, []);

  if (!mode) return <p className="text-muted">Select or create a board.</p>;

  async function save() {
    const rows = [
      {
        height: 360,
        widgets: selectedWidgetIds.map((wid, i) => {
          const w = widgets.find((x) => x.id === wid);
          return {
            width: 6,
            name: w?.name ?? `Widget ${wid}`,
            show: true,
            loading: false,
            widget: { id: wid },
          };
        }),
      },
    ];
    if (rows[0].widgets.length === 0 && widgets.length > 0) {
      const wid = widgets[0].id;
      rows[0].widgets = [
        { width: 12, name: widgets[0].name, show: true, loading: false, widget: { id: wid } },
      ];
    }
    const body = {
      id: item?.id,
      name,
      categoryId: categoryId || categories[0]?.categoryId,
      layout: { type: 'grid', rows },
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
      <div className="form-group">
        <label>Widgets in board</label>
        {widgets.map((w) => (
          <label key={w.id} style={{ display: 'block', fontWeight: 'normal' }}>
            <input
              type="checkbox"
              checked={selectedWidgetIds.includes(w.id)}
              onChange={(e) => {
                setSelectedWidgetIds((prev) =>
                  e.target.checked ? [...prev, w.id] : prev.filter((id) => id !== w.id),
                );
              }}
            />{' '}
            {w.name}
          </label>
        ))}
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
