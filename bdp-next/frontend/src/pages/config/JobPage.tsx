import { useEffect, useState } from 'react';
import { cboardDelete, cboardGet, cboardSave, type ServiceStatus } from '../../api/cboard';
import { translate } from '../../i18n/en';

type Job = { id: number; name: string; cronExp?: string; jobStatus?: number };

export function JobPage() {
  const [jobs, setJobs] = useState<Job[]>([]);
  const [name, setName] = useState('');
  const [cronExp, setCronExp] = useState('0 0 8 * * *');
  const [msg, setMsg] = useState<string | null>(null);

  const reload = () => cboardGet<Job[]>('getJobList').then(setJobs);

  useEffect(() => {
    reload();
  }, []);

  async function create() {
    const res = await cboardSave('saveJob', { name, cronExp, jobType: 'mail' });
    if (res.status === '1') {
      setName('');
      reload();
    } else setMsg(res.msg);
  }

  async function exec(id: number) {
    const res = await cboardGet<ServiceStatus>('execJob', { id });
    setMsg(res.status === '1' ? 'Job executed' : res.msg);
    reload();
  }

  return (
    <div id="inner-container" className="content">
      <section className="content-header">
        <h1>{translate('SIDEBAR.JOB')}</h1>
      </section>
      <div className="box box-solid">
        <div className="box-body">
          <div className="form-inline">
            <input className="form-control" placeholder="Job name" value={name} onChange={(e) => setName(e.target.value)} />
            <input className="form-control" style={{ marginLeft: 8 }} value={cronExp} onChange={(e) => setCronExp(e.target.value)} />
            <button type="button" className="btn btn-primary" style={{ marginLeft: 8 }} onClick={create}>
              {translate('COMMON.NEW')}
            </button>
          </div>
          {msg && <p className="text-muted">{msg}</p>}
          <ul className="list-group" style={{ marginTop: 16 }}>
            {jobs.map((j) => (
              <li key={j.id} className="list-group-item">
                {j.name} <small className="text-muted">{j.cronExp}</small>
                <span className="pull-right">
                  <button type="button" className="btn btn-xs btn-default" onClick={() => exec(j.id)}>Run</button>
                  <button type="button" className="btn btn-xs btn-danger" style={{ marginLeft: 4 }} onClick={() => cboardDelete('deleteJob', j.id).then(reload)}>Delete</button>
                </span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
