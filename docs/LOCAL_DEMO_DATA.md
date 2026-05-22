# Local demo data (H2 profile)

`-Denv=local` uses embedded H2 for CBoard **metadata** (`target/h2-local/metadata`) and a separate H2 file for **aggregation cache** (`target/h2-local/cboard`).

## What loads automatically

On each Tomcat start, Spring runs:

1. `h2-demo/h2.sql` — schema + login user **admin01** / **admin123** (business code **SH**)
2. `h2-demo/demo_seed.sql` — small FoodMart-style demo (~90 fact rows)

| Item | Name | Notes |
|------|------|--------|
| DataSource | `demo_source` | JDBC → same metadata H2 DB |
| Dataset | `foodmart_sample` | Query: `SELECT * FROM sales_fact_sample_flat` |
| Table | `sales_fact_sample_flat` | Sample sales rows |

After login, open **Configuration → DataSource**, open **demo_source**, run **Test** with SQL:

```sql
select 1;
```

Then **Configuration → Dataset** → **foodmart_sample** to build widgets/boards.

## Reset demo data

```bash
rm -rf target/h2-local
mvn clean package tomcat7:run -Denv=local
```

## Full sample (optional, ~5MB)

The upstream CBoard bundle includes `h2-demo/sample_data.sql` (thousands of rows). It is **not** loaded on every startup (slow, MySQL-specific syntax).

To load it once:

```bash
# Stop Tomcat first (H2 file lock)
chmod +x scripts/load-full-demo.sh
./scripts/load-full-demo.sh
mvn tomcat7:run -Denv=local
```

## PostgreSQL / external DB as DataSource

Demo seed only fills **H2**. To use PostgreSQL (e.g. `localhost:53254`):

1. Start PostgreSQL and create your schema/table.
2. **Configuration → DataSource → New**
3. Driver: `org.postgresql.Driver`
4. Run Tomcat with `mvn clean package tomcat7:run -Denv=local` so `postgresql-*.jar` is in `WEB-INF/lib`.

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `ERROR:org.postgresql.Driver` | Run `mvn clean package` before `tomcat7:run` |
| Login fails | `rm -rf target/h2-local`, restart with `-Denv=local` |
| DataSource test fails on H2 | JDBC URL must be `jdbc:h2:file:./target/h2-local/metadata;MODE=MySQL;...` (demo_source is preconfigured) |
| `sample_data.sql` syntax errors in IDE | Use `demo_seed.sql` or `scripts/load-full-demo.sh` (H2 conversion) |
