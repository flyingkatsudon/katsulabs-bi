# Docker MySQL (persistent metadata DB)

## H2 (`-Denv=local`) vs Docker MySQL (`-Denv=docker`)

| | `-Denv=local` (H2) | `-Denv=docker` (MySQL) |
|--|-------------------|------------------------|
| 메타 DB | **파일 H2** (`target/h2-local/metadata.mv.db`) | **MySQL 8** (Docker volume) |
| 인메모리? | 아니요 — 프로젝트 폴더에 파일로 저장 | 아니요 — `cboard_mysql_data` 볼륨 |
| Tomcat 재시작 | H2 파일 유지 (`rm -rf target/h2-local` 시 초기화) | MySQL 데이터 유지 |
| 집계 캐시 | `target/h2-local/cboard` (H2 파일) | `target/h2-docker/cboard` (H2 파일) |

`-Denv=local` 은 **인메모리 DB가 아닙니다.** 다만 DB 파일이 `target/` 아래에 있어 **프로젝트 삭제/클린 시 같이 지워질 수 있습니다.**

운영에 가깝게 쓰려면 **Docker MySQL** 프로필을 사용하세요.

## Quick start

```bash
# 1) MySQL (first time creates schema + admin + demo data)
docker compose up -d

# Wait until healthy
docker compose ps

# 2) App
mvn clean package tomcat7:run -Denv=docker
```

- URL: http://localhost:8080/bdp/login.jsp  
- Login: **SH** / **admin01** / **admin123**  
- Demo: **Configuration → DataSource → demo_source** (MySQL `cboard_demo.sales_fact_sample_flat`)

## Connection (from host)

| Key | Value |
|-----|--------|
| Host | `127.0.0.1:3306` |
| Database | `cboard_demo` |
| User / Password | `cboard` / `cboard` (root: `root`) |

App config: `src/main/resources/docker/config.properties` (activated by `-Denv=docker`).

## Reset database

```bash
docker compose down -v    # deletes volume cboard_mysql_data
docker compose up -d      # re-runs init scripts
```

## Switch back to H2

```bash
docker compose stop
mvn clean package tomcat7:run -Denv=local
```

## IntelliJ

Maven Runner VM/options or Goals:

```
clean package tomcat7:run -Denv=docker
```

## External PostgreSQL as DataSource

Metadata stays in Docker MySQL. Analytics DB (e.g. PostgreSQL on port 53254) is registered separately under **Configuration → DataSource** — see [LOCAL_DEMO_DATA.md](LOCAL_DEMO_DATA.md).
