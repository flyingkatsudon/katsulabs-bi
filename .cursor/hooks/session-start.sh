#!/usr/bin/env bash
# Lightweight session reminder for harness docs (fail-open).
set -euo pipefail

cat <<'EOF'
{"permission":"allow","agent_message":"Katsulabs BI harness: read AGENTS.md and docs/harness/AGENT-HIERARCHY.md before large changes. Legacy work follows docs/migration/04-legacy-layer-removal.md."}
EOF
exit 0
