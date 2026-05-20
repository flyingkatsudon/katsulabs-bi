import { FormEvent, useState } from 'react';

type TokenResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
};

export default function App() {
  const [token, setToken] = useState<string | null>(localStorage.getItem('bdp_token'));
  const [trends, setTrends] = useState<unknown[]>([]);
  const [error, setError] = useState<string | null>(null);

  async function onLogin(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    const form = new FormData(e.currentTarget);
    const res = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: form.get('username'),
        password: form.get('password'),
      }),
    });
    if (!res.ok) {
      setError('로그인 실패');
      return;
    }
    const data = (await res.json()) as TokenResponse;
    localStorage.setItem('bdp_token', data.accessToken);
    setToken(data.accessToken);
  }

  async function loadTrends() {
    if (!token) return;
    const res = await fetch('/api/v1/report/trends', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ fid: 'BDPC04030205' }),
    });
    setTrends(await res.json());
  }

  if (!token) {
    return (
      <main className="page">
        <h1>BDP Next</h1>
        <form onSubmit={onLogin}>
          <input name="username" placeholder="username" defaultValue="admin" />
          <input name="password" type="password" placeholder="password" defaultValue="admin" />
          <button type="submit">로그인</button>
        </form>
        {error && <p className="error">{error}</p>}
      </main>
    );
  }

  return (
    <main className="page">
      <h1>BDP 대시보드 (React)</h1>
      <button type="button" onClick={loadTrends}>
        키워드 트렌드 조회
      </button>
      <pre>{JSON.stringify(trends, null, 2)}</pre>
    </main>
  );
}
