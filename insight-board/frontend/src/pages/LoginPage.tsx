import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../api/client';

const BUSINESS_CODES = [
  { code: 'DEFAULT', name: 'Default' },
  { code: 'FIN', name: 'Financial' },
];

export function LoginPage() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    const form = new FormData(e.currentTarget);
    const username = String(form.get('username') ?? '');
    const password = String(form.get('password') ?? '');
    try {
      await login(username, password);
      navigate('/');
    } catch {
      setError('로그인에 실패했습니다. (admin / admin)');
    }
  }

  return (
    <div className="hold-transition login-page">
      <div className="login-box">
        <div className="login-logo">
          <span>
            <b>InsightBoard</b>
          </span>
        </div>
        <div className="login-box-body">
          <form onSubmit={onSubmit}>
            <div className="form-group has-feedback">
              <select className="form-control" name="businessCode" defaultValue="DEFAULT">
                {BUSINESS_CODES.map((b) => (
                  <option key={b.code} value={b.code}>
                    {b.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group has-feedback">
              <input
                type="text"
                className="form-control"
                name="username"
                placeholder="사&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;번"
                defaultValue="admin"
              />
              <span className="glyphicon glyphicon-user form-control-feedback" />
            </div>
            <div className="form-group has-feedback">
              <input
                type="password"
                className="form-control"
                name="password"
                placeholder="비밀번호"
                defaultValue="admin"
              />
              <span className="glyphicon glyphicon-lock form-control-feedback" />
            </div>
            <div className="row">
              <div className="col-xs-12">
                <button type="submit" className="btn btn-primary btn-block btn-flat">
                  로그인
                </button>
              </div>
            </div>
          </form>
          {error && <p className="text-danger" style={{ marginTop: 12 }}>{error}</p>}
        </div>
      </div>
    </div>
  );
}
