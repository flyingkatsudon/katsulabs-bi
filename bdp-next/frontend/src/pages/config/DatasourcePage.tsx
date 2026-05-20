import { useState } from 'react';
import { cboardDelete, cboardGet, cboardSave } from '../../api/cboard';
import { ConfigWorkspace, ListItem } from '../../components/ConfigWorkspace';
import { translate } from '../../i18n/en';

export function DatasourcePage() {
  return (
    <ConfigWorkspace
      titleKey="SIDEBAR.DATA_SOURCE"
      headerKey="CONFIG.DATA_SOURCE.DATA_SOURCE_HEADER"
      icon="fa-database"
      fetchList={() => cboardGet<ListItem[]>('getDatasourceList')}
      onDelete={(id) => cboardDelete('deleteDatasource', id).then(() => undefined)}
      renderForm={(item, mode, onSaved) => (
        <DatasourceForm item={item} mode={mode} onSaved={onSaved} />
      )}
    />
  );
}

function DatasourceForm({
  item,
  mode,
  onSaved,
}: {
  item: ListItem | null;
  mode: 'new' | 'edit' | null;
  onSaved: () => void;
}) {
  const [name, setName] = useState(item?.name ?? '');
  const [type, setType] = useState((item?.type as string) ?? 'jdbc');
  const [jdbcurl, setJdbcurl] = useState('jdbc:h2:mem:bdp');
  const [msg, setMsg] = useState<string | null>(null);

  if (!mode) {
    return <p className="text-muted">Select or create a datasource.</p>;
  }

  async function save() {
    const config = { driver: 'org.h2.Driver', jdbcurl, username: 'sa', password: '' };
    const body = { id: item?.id, name, type, config };
    const res = await cboardSave(mode === 'new' ? 'saveNewDatasource' : 'updateDatasource', body);
    if (res.status === '1') onSaved();
    else setMsg(res.msg);
  }

  return (
    <div>
      <h3 className="box-title">{mode === 'new' ? translate('COMMON.NEW') : translate('COMMON.EDIT')}</h3>
      <div className="form-group">
        <label>Name</label>
        <input className="form-control" value={name} onChange={(e) => setName(e.target.value)} />
      </div>
      <div className="form-group">
        <label>Type</label>
        <select className="form-control" value={type} onChange={(e) => setType(e.target.value)}>
          <option value="jdbc">jdbc</option>
          <option value="textfile">textfile</option>
          <option value="h2">h2</option>
        </select>
      </div>
      <div className="form-group">
        <label>JDBC URL</label>
        <input className="form-control" value={jdbcurl} onChange={(e) => setJdbcurl(e.target.value)} />
      </div>
      <button type="button" className="btn btn-primary" onClick={save}>
        {translate('COMMON.SAVE')}
      </button>
      {msg && <p className="text-danger">{msg}</p>}
    </div>
  );
}
