#!/usr/bin/env bash
# Block destructive git commands (aligns with User Rules git safety).
set -euo pipefail

input=$(cat)

deny() {
  local msg="$1"
  printf '%s\n' "{\"permission\":\"deny\",\"user_message\":\"$msg\",\"agent_message\":\"Blocked by katsulabs-bi pre-shell-guard hook.\"}"
  exit 2
}

# Extract command without requiring jq
command=""
if command -v python3 >/dev/null 2>&1; then
  command=$(printf '%s' "$input" | python3 -c 'import sys,json; print(json.load(sys.stdin).get("command",""))' 2>/dev/null || true)
fi
if [[ -z "$command" ]]; then
  command="$input"
fi

if [[ "$command" =~ git[[:space:]]+push[[:space:]].*--force ]] ||
   [[ "$command" =~ git[[:space:]]+push[[:space:]]+-f ]] ||
   [[ "$command" =~ git[[:space:]]+reset[[:space:]]+--hard ]] ||
   [[ "$command" =~ git[[:space:]]+clean[[:space:]]+-fd ]] ||
   [[ "$command" =~ git[[:space:]]+checkout[[:space:]]+--[[:space:]]+\. ]] ||
   [[ "$command" =~ git[[:space:]]+config[[:space:]] ]]; then
  deny "Destructive or git-config git command blocked. Ask the user explicitly if this is required."
fi

printf '%s\n' '{"permission":"allow"}'
exit 0
