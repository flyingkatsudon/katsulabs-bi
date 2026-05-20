import { FormEvent, useEffect, useState } from 'react';
import { apiGet } from '../../api/client';
import { translate } from '../../i18n/en';

type Category = { categoryId: number; categoryName: string };

export function CategoryPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [name, setName] = useState('');

  const load = () => apiGet<Category[]>('/cboard/dashboard/getCategoryList').then(setCategories);

  useEffect(() => {
    load();
  }, []);

  async function onSave(e: FormEvent) {
    e.preventDefault();
    const json = encodeURIComponent(JSON.stringify({ name }));
    await fetch(`/cboard/dashboard/saveNewCategory?json=${json}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('insightboard_token')}` },
    });
    setName('');
    load();
  }

  return (
    <div id="inner-container" className="content">
      <section className="content-header">
        <h1>{translate('SIDEBAR.DASHBOARD_CATEGORY')}</h1>
      </section>
      <div className="row">
        <div className="col-md-6">
          <div className="box box-solid">
            <div className="box-header with-border">
              <h3 className="box-title">{translate('CONFIG.CATEGORY.CATEGORY_HEADER')}</h3>
            </div>
            <div className="box-body">
              <form onSubmit={onSave} className="form-inline">
                <input
                  className="form-control"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Category name"
                />
                <button type="submit" className="btn btn-primary" style={{ marginLeft: 8 }}>
                  {translate('COMMON.NEW')}
                </button>
              </form>
              <ul className="list-group" style={{ marginTop: 16 }}>
                {categories.map((c) => (
                  <li key={c.categoryId} className="list-group-item">
                    {c.categoryName}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
