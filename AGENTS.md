# AGENTS.md

## Cursor Cloud specific instructions

### Project overview
This is a customized CBoard BI dashboard (Shinhan BDP) — a Java 8 / Spring 4.3 / AngularJS web application packaged as a WAR and deployed on Tomcat 8.5. See `pom.xml` for dependencies and `src/main/webapp/WEB-INF/web.xml` for servlet config.

### Prerequisites (installed by update script)
- **JDK 8** (`/usr/lib/jvm/java-8-openjdk-amd64`) — required by `pom.xml` (`jdk.version=1.8`)
- **Apache Maven** — builds the WAR
- **MySQL 8** — CBoard metadata store (`cboard_demo` database)
- **Tomcat 8.5** — installed at `/opt/apache-tomcat`

### Build
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn clean package -DskipTests
```

### Known gotchas
- **Owlnest stub JAR**: The `com.owlnest` package is a proprietary library not available publicly. A stub JAR (`lib/owlnest-stub.jar`) is provided for compilation. The `pom.xml` has a system-scope dependency pointing to it. Without this, the Shinhan GA module (`GeneralAnalyticsBizImpl`, `DocTableImpl`) will not compile.
- **Jackson / Avatica conflict**: `avatica-1.8.0.jar` (transitive from `kylin-jdbc`) bundles an old `com.fasterxml.jackson.annotation.JsonFormat` that lacks the `empty()` method. After building and deploying the WAR, you must strip the `com/fasterxml/` directory from `avatica-1.8.0.jar` inside the deployed WAR's `WEB-INF/lib/`. The deploy script below handles this.
- **Maven HTTP repository blocker**: Maven 3.8+ blocks HTTP repositories. A `~/.m2/settings.xml` that disables the default HTTP blocker is needed (the update script sets this up). The `pom.xml` references `http://maven.aliyun.com` which requires this.
- **sqljdbc4 not in Maven Central**: `com.microsoft.sqlserver:sqljdbc4:4.0` is in `lib/sqljdbc4-4.0.jar`. It must be installed to the local Maven repo before build.

### Running the application
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Start MySQL if not running
sudo mysqld_safe &

# Build
mvn clean package -DskipTests

# Deploy
sudo cp target/bdp.war /opt/apache-tomcat/webapps/

# Fix avatica Jackson conflict in the exploded WAR
cd /opt/apache-tomcat/webapps/bdp/WEB-INF/lib
mkdir -p /tmp/avatica-fix && cp avatica-1.8.0.jar /tmp/avatica-fix/
cd /tmp/avatica-fix && jar xf avatica-1.8.0.jar && rm avatica-1.8.0.jar
rm -rf com/fasterxml && jar cf avatica-1.8.0.jar .
sudo cp avatica-1.8.0.jar /opt/apache-tomcat/webapps/bdp/WEB-INF/lib/

# Start Tomcat
/opt/apache-tomcat/bin/catalina.sh run
```
App is at `http://localhost:8080/bdp/`.

### Test credentials
- Business code: **SH** (신한은행)
- User ID: **admin01**
- Password: **admin123**

### Running tests
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn test
```

### PostgreSQL / external services
The Shinhan analytics module (`com.shinhan`) connects to an external PostgreSQL database and the `com.owlnest` analytics API. These are unavailable in the dev environment, so Insight Report and General Analysis endpoints will fail at runtime, but the core CBoard dashboard functionality works.
