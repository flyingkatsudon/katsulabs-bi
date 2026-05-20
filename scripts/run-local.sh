#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

export PATH="${HOME}/.local/maven/bin:${PATH}"
export MAVEN_OPTS="${MAVEN_OPTS:-} --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"

mkdir -p data

mvn -q clean package -DskipTests -Denv=local
exec mvn -q org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Denv=local
