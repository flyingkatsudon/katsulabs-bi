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
- **Jackson / Avatica conflict**: `avatica-1.8.0.jar` (transitive from `kylin-jdbc`) bundles an old `com.fasterxml.jackson.annotation.JsonFormat` that lacks the `empty()` method. After building and deploying the WAR, you must strip the `com/fasterxml/` directory from `avatica-1.8.0.jar` inside the deployed WAR's `WEB-INF/lib/`. The deploy script below handles this.
- **Maven HTTP repository blocker**: Maven 3.8+ blocks HTTP repositories. A `~/.m2/settings.xml` that disables the default HTTP blocker is needed (the update script sets this up). The `pom.xml` references `http://maven.aliyun.com` which requires this.
- **sqljdbc4 not in Maven Central**: `com.microsoft.sqlserver:sqljdbc4:4.0` is in `lib/sqljdbc4-4.0.jar`. It must be installed to the local Maven repo before build.

### H2 local profile (no MySQL)

Use embedded H2 instead of MySQL by setting the Maven `env` property:

```bash
# IntelliJ Maven goal: add -Denv=local  (or -Denv=h2)
mvn clean package tomcat7:run -Denv=local

# optional shell wrapper:
export BDP_ENV=local
mvn clean package tomcat7:run -Denv=${BDP_ENV}
```

`package` is required so JDBC drivers (PostgreSQL, MySQL, etc.) are packaged into `WEB-INF/lib`. Without it, datasource **Test** may fail with `ERROR:org.postgresql.Driver`.

- Metadata DB: `jdbc:h2:file:./target/h2-local/metadata` (H2 1.4.x; isolated from `~/H2Data`)
- Login: business code **SH**, user **admin01**, password **admin123**
- On first start, `h2-demo/h2.sql` + `h2-demo/demo_seed.sql` seed schema, admin user, and demo DataSource/dataset (`demo_source` / `foodmart_sample`)
- Demo guide: [docs/LOCAL_DEMO_DATA.md](docs/LOCAL_DEMO_DATA.md)
- Reset DB: `rm -rf target/h2-local` then restart Tomcat
- JDK 17+ for `tomcat7:run`: `.mvn/jvm.config` supplies `--add-opens` (not used on JDK 8)
- If you previously used `~/H2Data` with a newer H2 client, delete that folder or ignore it — this project no longer uses that path for `-Denv=local`

### Running the application (MySQL)
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

### Database schema setup (first-time only)
The base schema at `sql/mysql/mysql.sql` is incomplete for the Shinhan customization. After loading it, you must add these columns:
```sql
ALTER TABLE dashboard_user ADD COLUMN business_code VARCHAR(10) DEFAULT '' AFTER user_id;
ALTER TABLE dashboard_user ADD COLUMN rbac_policy VARCHAR(255) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN user_state_info VARCHAR(255) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN del_cd VARCHAR(10) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN user_last_date DATETIME DEFAULT NULL;
ALTER TABLE dashboard_user_role ADD COLUMN business_code VARCHAR(10) DEFAULT '' AFTER user_id;
ALTER TABLE dashboard_role_res ADD COLUMN del_cd VARCHAR(10) DEFAULT 'N';
```
Then seed the admin user:
```sql
INSERT INTO dashboard_role (role_id, role_name, user_id) VALUES ('1', 'Admin', 'admin01');
INSERT INTO dashboard_user (user_id, business_code, login_name, user_name, user_password, user_status, rbac_policy, user_state_info, del_cd)
  VALUES ('admin01', 'SH', 'admin01', 'Admin', '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9', 'active', '0', '0', 'N');
INSERT INTO dashboard_user_role (user_id, business_code, role_id) VALUES ('admin01', 'SH', '1');
INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd) VALUES ('1', '1', 1, 'write', 'N');
```
The password hash is `SHA-256("admin123").toUpperCase()`.

### MySQL startup notes
- Start with: `sudo mysqld_safe &` (wait ~5s for port 3306 to be ready)
- Fix socket permissions if needed: `sudo chmod 755 /var/run/mysqld`
- The app connects as the Jasypt-decrypted user (key=`111111`). In dev, create `root@localhost` with empty password and grant on `cboard_demo`.

### Avatica fix requires sudo
Tomcat deploys the WAR as root. Use `sudo cp` when copying the avatica JAR from the exploded WAR, and `$JAVA_HOME/bin/jar` (not system `jar`) for JDK 8 compatibility.

### Removed Shinhan module
The legacy `com.shinhan` Insight Report / General Analytics backend has been removed from this fork. Static files under `src/main/webapp/report/` may remain but `/report/*` API endpoints are no longer served. Use the core CBoard dashboard (`/bdp/cboard/starter.jsp`) after login.
