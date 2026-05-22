#!/usr/bin/env bash
# 로컬 dev 서버(API·Vite) 강제 종료 — bootRun/Ctrl+C 후 포트가 남을 때 사용
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PORTS=(8081 8082 8083 5173)

stop_port() {
  local port="$1"
  local pids
  pids="$(lsof -ti ":$port" 2>/dev/null || true)"
  if [[ -z "$pids" ]]; then
    return
  fi
  echo "Stopping port $port (PID $pids)…"
  kill -TERM $pids 2>/dev/null || true
}

for port in "${PORTS[@]}"; do
  stop_port "$port"
done

sleep 1

for port in "${PORTS[@]}"; do
  pids="$(lsof -ti ":$port" 2>/dev/null || true)"
  if [[ -n "$pids" ]]; then
    echo "Force kill port $port (PID $pids)…"
    kill -KILL $pids 2>/dev/null || true
  fi
done

pkill -f 'com.katsulabs.bi.KatsulabsBiApplication' 2>/dev/null || true
pkill -f 'gradle.*:modules:api:bootRun' 2>/dev/null || true

rm -f "$ROOT/.run/api.pid" "$ROOT/.run/api.port"

if [[ "${1:-}" == "--gradle" ]]; then
  (cd "$ROOT" && ./gradlew --stop) || true
fi

echo "Done. Remaining listeners:"
lsof -i :8081 -i :8082 -i :8083 -i :5173 2>/dev/null || echo "  (none)"
