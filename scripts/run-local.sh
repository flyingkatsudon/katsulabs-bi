#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

export PATH="${HOME}/.local/maven/bin:${PATH}"
mkdir -p data

mvn -q clean package -DskipTests -Denv=local
# Tomcat fork JVM args are configured in pom.xml (tomcat7-maven-plugin jvmArgs)
exec mvn -q org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run -Denv=local
