#!/usr/bin/env bash
# AdminLTE dist/ (layout CSS, skins, avatars) — required by index.html.
# Restores from git when legacy WAR webapp is no longer in the tree.

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
DEST="$ROOT/frontend/public/katsulabs-bi/dist"
REF="${1:-621d16f}"

if git -C "$ROOT" cat-file -e "$REF^{commit}" 2>/dev/null; then
  :
else
  echo "Unknown git ref: $REF"
  exit 1
fi

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

# REF 시점의 제거된 WAR 트리 경로 (과거 커밋 전용)
git -C "$ROOT" archive "$REF" src/main/webapp/cboard/dist | tar -x -C "$TMP"
rm -rf "$DEST"
mkdir -p "$(dirname "$DEST")"
mv "$TMP/src/main/webapp/cboard/dist" "$DEST"
echo "Restored AdminLTE dist to $DEST (from $REF)"
