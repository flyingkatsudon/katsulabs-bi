import { useState } from 'react';
import { cboardDelete, cboardGet, cboardPost, cboardSave } from '../../api/cboard';
import { ConfigWorkspace, ListItem } from '../../components/ConfigWorkspace';
import { translate } from '../../i18n/en';

export function DatasetPage() {
  return (
    <ConfigWorkspace
      titleKey="SIDEBAR.DATASET"
      headerKey="CONFIG.DATASET.DATASET_HEADER"
      icon="fa-table"
      fetchList={() => cboardGet<ListItem[]>('getDatasetList')}
      onDelete={(id) => cboardDelete('deleteDataset', id).then(() => undefined)}
      renderForm={(item, mode, onSaved) => <DatasetForm item={item} mode={mode} onSaved={onSaved} />}
    />
  );
}

function DatasetForm({
  item,
  mode,
  onSaved,
}: {
  item: ListItem | null;
  mode: 'new' | 'edit' | null;
  onSaved: () => void;
}) {
  const [name, setName] = useState(item?.name ?? '');
  const [categoryName, setCategoryName] = useState((item?.categoryName as string) ?? 'default');
  const [schema, setSchema] = useState(JSON.stringify(item?.data ?? { schema: { dimension: [], measure: [] } }, null, 2));

  if (!mode) return <p className="text-muted">Select or create a dataset.</p>;

  async function save() {
    let data;
    try {
      data = JSON.parse(schema);
    } catch {
      alert('Invalid JSON');
      return;
    }
    await cboardSave(mode === 'new' ? 'saveNewDataset' : 'updateDataset', {
      id: item?.id,
      name,
      categoryName,
      data,
    });
    onSaved();
  }

  async function loadColumns() {
    const result = await cboardPost<{ columns: string[]; data: string[][] }>('getColumns', {
      datasetId: item?.id ?? 1,
    });
    alert(`Columns: ${result.columns?.join(', ')}\nSample rows: ${result.data?.length ?? 0}`);
  }

  async function previewAgg() {
    const result = await cboardPost<unknown>('getAggregateData', { datasetId: item?.id ?? 1, cfg: '{}' });
    alert(JSON.stringify(result, null, 2).slice(0, 800));
  }

  return (
    <div>
      <h3>{translate('SIDEBAR.DATASET')}</h3>
      <div className="form-group">
        <label>Name</label>
        <input className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Category</label>
        <input className="form-control" value={categoryName} onChange={(e) => setCategoryName(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Schema JSON</label>
        <textarea className="form-control" rows={12} value={schema} onChange={(e) => setSchema(e.target.value)} />
      </div>
      <button type="button" className="btn btn-primary" onClick={save}>
        {translate('COMMON.SAVE')}
      </button>
      <button type="button" className="btn btn-default" style={{ marginLeft: 8 }} onClick={loadColumns}>
        Load columns
      </button>
      <button type="button" className="btn btn-default" style={{ marginLeft: 8 }} onClick={previewAgg}>
        Preview aggregate
      </button>
    </div>
  );
}
