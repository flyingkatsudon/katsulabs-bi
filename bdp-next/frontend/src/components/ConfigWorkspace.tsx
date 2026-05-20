import { ReactNode, useEffect, useState } from 'react';
import { translate } from '../i18n/en';

export type ListItem = { id: number; name: string; [key: string]: unknown };

type Props = {
  titleKey: string;
  headerKey: string;
  icon: string;
  fetchList: () => Promise<ListItem[]>;
  renderForm: (item: ListItem | null, mode: 'new' | 'edit' | null, onSaved: () => void) => ReactNode;
  onDelete?: (id: number) => Promise<void>;
};

export function ConfigWorkspace({ titleKey, headerKey, icon, fetchList, renderForm, onDelete }: Props) {
  const [items, setItems] = useState<ListItem[]>([]);
  const [selected, setSelected] = useState<ListItem | null>(null);
  const [mode, setMode] = useState<'new' | 'edit' | null>(null);

  const reload = () => fetchList().then(setItems);

  useEffect(() => {
    reload();
  }, []);

  const onSaved = () => {
    setMode(null);
    setSelected(null);
    reload();
  };

  return (
    <div id="inner-container" className="content">
      <section className="content-header">
        <h1>{translate(titleKey)}</h1>
      </section>
      <div className="row">
        <div className="col-md-3">
          <div className="box box-solid">
            <div className="box-header with-border">
              <i className={`fa ${icon}`} /> <h3 className="box-title">{translate(headerKey)}</h3>
              <div className="box-tools pull-right">
                <i
                  className="fa fa-plus toolbar-icon"
                  title={translate('COMMON.NEW')}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    setSelected(null);
                    setMode('new');
                  }}
                />
              </div>
            </div>
            <div className="box-body no-padding">
              <ul className="nav nav-pills nav-stacked">
                {items.map((o) => (
                  <li key={o.id} className={selected?.id === o.id ? 'active' : ''}>
                    <a
                      href="#"
                      onClick={(e) => {
                        e.preventDefault();
                        setSelected(o);
                        setMode('edit');
                      }}
                    >
                      {o.name}
                      <span className="pull-right text-red">
                        <i
                          className="fa fa-trash-o"
                          style={{ cursor: 'pointer' }}
                          onClick={async (e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            if (onDelete && confirm('Delete?')) {
                              await onDelete(o.id);
                              onSaved();
                            }
                          }}
                        />
                      </span>
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
        <div className="col-md-9">
          <div className="box">
            <div className="box-body">{renderForm(selected, mode, onSaved)}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
