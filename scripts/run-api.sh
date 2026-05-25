#!/usr/bin/env bash
# API 단독 실행 (권장): Ctrl+C 가 JVM 에 직접 전달되어 종료가 확실함
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PROFILE="${PROFILE:-local-h2}"
PORT="${API_PORT:-8081}"

mkdir -p "$ROOT/.run"

echo "Building bootJar…"
./gradlew :modules:api:bootJar -q

JAR="$(ls -t "$ROOT/modules/api/build/libs"/insight-board-*.jar 2>/dev/null | head -1)"
if [[ -z "$JAR" || ! -f "$JAR" ]]; then
  echo "bootJar not found under modules/api/build/libs/" >&2
  exit 1
fi

echo "Starting $JAR (profile=$PROFILE port=$PORT)"
echo "Stop: Ctrl+C or ./scripts/stop-dev.sh"

exec java -jar "$JAR" --spring.profiles.active="$PROFILE" --server.port="$PORT"
