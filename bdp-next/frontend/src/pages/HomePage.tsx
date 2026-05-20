import { translate } from '../i18n/en';

export function HomePage() {
  return (
    <>
      <section className="content-header">
        <h1>
          {translate('HOMEPAGE.TITLE')}
          <small>{translate('HOMEPAGE.QUICK_START')}</small>
        </h1>
        <ol className="breadcrumb">
          <li>
            <a href="/">
              <i className="fa fa-dashboard" /> {translate('HOMEPAGE.LINK_CAPTION')}
            </a>
          </li>
        </ol>
      </section>
      <div id="inner-container" className="content">
        <div className="row">
          <div className="col-md-4">
            <div className="box box-solid">
              <div className="box-header with-border">
                <i className="fa fa-cubes" />{' '}
                <h3 className="box-title">
                  {translate('HOMEPAGE.CUBES')}{' '}
                  <small>{translate('HOMEPAGE.CUBES_TIP')}</small>
                </h3>
              </div>
              <div className="panel-body" style={{ overflowY: 'auto', minHeight: 320 }}>
                <p className="text-muted">
                  React CBoard UI — select a dashboard from the left menu or open Configuration to manage
                  datasources, datasets, and widgets.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
