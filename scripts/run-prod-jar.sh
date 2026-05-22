#!/usr/bin/env bash
# Build frontend + bootJar and run with prod SPA profile (port 8081).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ ! -f frontend/package-lock.json ]]; then
  echo "frontend/package-lock.json not found" >&2
  exit 1
fi

echo "==> npm ci + build (frontend)"
(cd frontend && npm ci && npm run build)

echo "==> bootJar (embed frontend/dist when present)"
./gradlew :modules:api:bootJar --no-daemon

JAR="$ROOT/modules/api/build/libs/katsulabs-bi.jar"
if [[ ! -f "$JAR" ]]; then
  echo "JAR not found: $JAR" >&2
  exit 1
fi

echo "==> java -jar (profiles: local-h2,prod)"
exec java -jar "$JAR" --spring.profiles.active=local-h2,prod
