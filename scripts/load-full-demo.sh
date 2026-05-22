#!/usr/bin/env bash
# Load the full CBoard FoodMart sample (~5MB) into local H2 metadata DB.
# Requires: JDK with jar, Maven H2 dependency on disk.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

H2_JAR="$(find "$HOME/.m2/repository/com/h2database/h2" -name 'h2-1.4.196.jar' 2>/dev/null | head -1)"
if [[ -z "$H2_JAR" ]]; then
  echo "H2 1.4.196 JAR not found. Run: mvn -q dependency:resolve -DincludeArtifactIds=h2"
  exit 1
fi

DB_URL="jdbc:h2:file:./target/h2-local/metadata;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
SCRIPT_SRC="$ROOT/src/main/resources/h2-demo/sample_data.sql"
SCRIPT_TMP="$(mktemp)"
trap 'rm -f "$SCRIPT_TMP"' EXIT

# H2-compatible copy: drop MySQL backticks and display widths
sed -e 's/`//g' \
    -e 's/int([0-9]*)/INT/g' \
    -e 's/smallint([0-9]*)/SMALLINT/g' \
    -e 's/datetime/TIMESTAMP/g' \
    -e 's/ DEFAULT CHARSET=utf8//g' \
    -e 's|jdbc:h2:~/H2Data/metadata[^"]*|jdbc:h2:file:./target/h2-local/metadata;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE|g' \
    -e "s/VALUES (1,'1',/VALUES (1,'admin01',/" \
    -e "s/,1,'Default/,1,'admin01','Default/" \
    "$SCRIPT_SRC" > "$SCRIPT_TMP"

echo "Loading full demo data (may take 1-2 minutes)..."
java -cp "$H2_JAR" org.h2.tools.RunScript \
  -url "$DB_URL" -user sa -script "$SCRIPT_TMP" -continueOnError 2>&1 | tail -5

echo "Done. Restart Tomcat and open Configuration → DataSource → demo_source → Test."
