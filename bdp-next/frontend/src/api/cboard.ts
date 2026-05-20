import { apiGet, getToken } from './client';

export type ServiceStatus = { status: string; msg: string };

export async function cboardGet<T>(path: string, params?: Record<string, string | number | undefined>): Promise<T> {
  const qs = new URLSearchParams();
  if (params) {
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) qs.set(k, String(v));
    });
  }
  const q = qs.toString();
  return apiGet<T>(`/cboard/dashboard/${path}${q ? `?${q}` : ''}`);
}

export async function cboardSave(path: string, json: unknown): Promise<ServiceStatus> {
  const token = getToken();
  const url = `/cboard/dashboard/${path}?json=${encodeURIComponent(JSON.stringify(json))}`;
  const res = await fetch(url, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<ServiceStatus>;
}

export async function cboardPost<T>(
  path: string,
  params?: Record<string, string | number | boolean | undefined>,
): Promise<T> {
  const qs = new URLSearchParams();
  if (params) {
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null) qs.set(k, String(v));
    });
  }
  const q = qs.toString();
  const token = getToken();
  const res = await fetch(`/cboard/dashboard/${path}${q ? `?${q}` : ''}`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<T>;
}

export async function cboardDelete(path: string, id: number): Promise<ServiceStatus> {
  const token = getToken();
  const url = `/cboard/dashboard/${path}?id=${id}`;
  const res = await fetch(url, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<ServiceStatus>;
}
