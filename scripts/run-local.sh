#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

export PATH="${HOME}/.local/maven/bin:${PATH}"
mkdir -p data

mvn -q clean package -DskipTests -Denv=local
# JVM --add-opens: see .mvn/jvm.config (tomcat7 plugin does not support jvmArgs)
exec mvn -q org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Denv=local
