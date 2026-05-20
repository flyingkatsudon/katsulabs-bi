import { useEffect, useState } from 'react';
import { apiGet } from '../../api/client';
import { translate } from '../../i18n/en';

type ListItem = { id: number; name: string; type?: string; categoryName?: string };

type Props = {
  titleKey: string;
  headerKey: string;
  fetchPath: string;
  icon: string;
};

export function ListConfigPage({ titleKey, headerKey, fetchPath, icon }: Props) {
  const [items, setItems] = useState<ListItem[]>([]);

  useEffect(() => {
    apiGet<ListItem[]>(fetchPath).then(setItems);
  }, [fetchPath]);

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
                <i className="fa fa-plus toolbar-icon" title={translate('COMMON.NEW')} style={{ cursor: 'pointer' }} />
              </div>
            </div>
            <div className="box-body no-padding">
              <ul className="nav nav-pills nav-stacked">
                {items.map((o) => (
                  <li key={o.id}>
                    <a href="#">
                      {o.name}
                      <span className="pull-right text-red">
                        <i className="fa fa-info" style={{ cursor: 'pointer' }} /> &nbsp;
                        <i className="fa fa-copy" style={{ cursor: 'pointer' }} /> &nbsp;
                        <i className="fa fa-edit" style={{ cursor: 'pointer' }} /> &nbsp;
                        <i className="fa fa-trash-o" style={{ cursor: 'pointer' }} />
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
            <div className="box-body">
              <p className="text-muted">Select an item from the list to edit (full editor in next iteration).</p>
              <pre style={{ maxHeight: 400, overflow: 'auto' }}>{JSON.stringify(items, null, 2)}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
