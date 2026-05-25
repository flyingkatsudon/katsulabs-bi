#!/usr/bin/env bash
# 레거시 webapp/cboard 정적 자산을 frontend/public/cboard 로 동기화
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
SRC="$ROOT/src/main/webapp/cboard"
DEST="$ROOT/frontend/public/cboard"

if [[ ! -d "$SRC" ]]; then
  echo "Missing source: $SRC" >&2
  exit 1
fi

mkdir -p "$DEST"

# 핵심 UI (CSS/JS/폰트)
for item in bootstrap css dist fonts imgs plugins/jQuery plugins/iCheck; do
  if [[ -e "$SRC/$item" ]]; then
    dest_parent="$(dirname "$DEST/$item")"
    mkdir -p "$dest_parent"
    rsync -a --delete "$SRC/$item/" "$DEST/$item/"
  fi
done

if [[ -f "$SRC/plugins/jstree.min.js" ]]; then
  mkdir -p "$DEST/plugins"
  cp -f "$SRC/plugins/jstree.min.js" "$DEST/plugins/"
fi

# starter.jsp 에서 링크하는 추가 플러그인 CSS
for p in ngJsTree jQueryUI rzslider daterangepicker timeline tree jquery-contextmenu colorpicker; do
  if [[ -d "$SRC/plugins/$p" ]]; then
    mkdir -p "$DEST/plugins"
    rsync -a "$SRC/plugins/$p/" "$DEST/plugins/$p/"
  fi
done

echo "Synced cboard assets to $DEST"
