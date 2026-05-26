#!/usr/bin/env bash
# AdminLTE 정적 자산은 frontend/public/katsulabs-bi 에 유지합니다.
# (외부 레거시 WAR 배포본에서 복사할 때만 이 스크립트 사용)

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
DEST="$ROOT/frontend/public/katsulabs-bi"
SRC="${1:-}"

if [[ -z "$SRC" ]]; then
  echo "Usage: $0 /path/to/legacy-webapp/katsulabs-bi"
  echo "Example: copy from an exploded WAR legacy webapp static directory."
  echo "Current assets live at: $DEST"
  exit 1
fi

if [[ ! -d "$SRC" ]]; then
  echo "Source not found: $SRC"
  exit 1
fi

mkdir -p "$DEST"
rsync -a --delete \
  --exclude='org/' \
  --exclude='plugins/ckeditor/' \
  --exclude='plugins/datatables/' \
  --exclude='plugins/flot/' \
  --exclude='plugins/morris/' \
  --exclude='plugins/datepicker/' \
  --exclude='plugins/input-mask/' \
  "$SRC/" "$DEST/"

if [[ -f "$DEST/css/katsulabs-bi.css (legacy rename)" && ! -f "$DEST/css/katsulabs-bi.css" ]]; then
  mv "$DEST/css/katsulabs-bi.css (legacy rename)" "$DEST/css/katsulabs-bi.css"
fi

echo "Synced into $DEST"
