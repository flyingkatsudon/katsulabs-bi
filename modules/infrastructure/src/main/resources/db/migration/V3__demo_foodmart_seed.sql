-- Boot local-h2: 집계/미리보기 데모 (sales_fact_sample_flat + demo_source + foodmart_sample)

CREATE TABLE IF NOT EXISTS sales_fact_sample_flat (
  the_year INT,
  month_of_year SMALLINT,
  day_of_month SMALLINT,
  the_date TIMESTAMP,
  SALES_DISTRICT VARCHAR(30),
  SALES_REGION VARCHAR(30),
  SALES_COUNTRY VARCHAR(30),
  yearly_income VARCHAR(30) NOT NULL,
  total_children SMALLINT NOT NULL,
  member_card VARCHAR(30),
  num_cars_owned INT,
  gender VARCHAR(30) NOT NULL,
  store_sales DECIMAL(10,4) NOT NULL,
  store_cost DECIMAL(10,4) NOT NULL,
  unit_sales DECIMAL(10,4) NOT NULL
);

DELETE FROM sales_fact_sample_flat;

INSERT INTO sales_fact_sample_flat VALUES
(2016,2,26,TIMESTAMP '2016-02-26 05:00:00','Salem','North West','USA','$30K - $50K',4,'Bronze',2,'F',8.7900,4.1313,3.0000),
(2017,8,8,TIMESTAMP '2017-08-08 05:00:00','Salem','North West','USA','$30K - $50K',1,'Bronze',3,'M',6.9000,2.8980,3.0000),
(2017,6,19,TIMESTAMP '2017-06-19 05:00:00','Seattle','North West','USA','$30K - $50K',0,'Bronze',2,'F',11.3600,4.3168,4.0000),
(2016,11,4,TIMESTAMP '2016-11-04 05:00:00','Seattle','North West','USA','$10K - $30K',3,'Normal',2,'M',8.4000,2.9400,5.0000),
(2017,5,7,TIMESTAMP '2017-05-07 05:00:00','Salem','North West','USA','$50K - $70K',3,'Bronze',4,'M',2.4200,0.8470,2.0000);

INSERT INTO dashboard_datasource (user_id, source_name, source_type, config)
VALUES (
    'admin01',
    'demo_source',
    'jdbc',
    '{"aggregateProvider":true,"password":"","pooled":true,"driver":"org.h2.Driver","jdbcurl":"jdbc:h2:mem:insight-board;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE","username":"sa"}'
);

INSERT INTO dashboard_dataset (user_id, category_name, dataset_name, data_json)
VALUES (
    'admin01',
    'Default Category',
    'foodmart_sample',
    '{"schema":{"measure":[{"column":"store_sales","id":"m1","type":"column"},{"column":"store_cost","id":"m2","type":"column"}],"dimension":[{"column":"the_year","id":"d1","type":"column"},{"column":"SALES_COUNTRY","id":"d2","type":"column"}]},"selects":["the_year","SALES_COUNTRY","store_sales","store_cost"],"datasource":1,"query":{"sql":"SELECT * FROM sales_fact_sample_flat"},"filters":[],"expressions":[]}'
);

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '2', d.dataset_id, 'write', 'N'
  FROM dashboard_dataset d
 WHERE d.dataset_name = 'foodmart_sample'
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_role_res r
        WHERE r.role_id = '1' AND r.res_type = '2' AND r.res_id = d.dataset_id
   );
