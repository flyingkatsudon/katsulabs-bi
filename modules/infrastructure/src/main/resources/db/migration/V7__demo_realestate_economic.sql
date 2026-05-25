-- 부동산·경제 지표 데모 테이블 + DataSource + Dataset (FoodMart 와 동일 H2 메모리 DB)

CREATE TABLE IF NOT EXISTS real_estate_sample (
  deal_year INT NOT NULL,
  deal_quarter SMALLINT NOT NULL,
  city VARCHAR(40) NOT NULL,
  district VARCHAR(40) NOT NULL,
  property_type VARCHAR(30) NOT NULL,
  avg_price_100m DECIMAL(12,2) NOT NULL,
  transaction_count INT NOT NULL,
  avg_area_sqm DECIMAL(10,2) NOT NULL
);

DELETE FROM real_estate_sample;

INSERT INTO real_estate_sample VALUES
(2023,1,'Seoul','Gangnam','Apartment',1850.50,420,84.2),
(2023,1,'Seoul','Mapo','Apartment',1120.30,310,72.5),
(2023,2,'Seoul','Gangnam','Apartment',1920.00,445,84.0),
(2023,2,'Busan','Haeundae','Apartment',680.40,280,79.1),
(2023,3,'Seoul','Songpa','Apartment',1580.20,390,81.3),
(2023,3,'Incheon','Yeonsu','Apartment',520.60,195,74.8),
(2023,4,'Seoul','Gangnam','Officetel',980.10,510,33.2),
(2024,1,'Seoul','Gangnam','Apartment',2010.80,460,83.9),
(2024,1,'Gyeonggi','Seongnam','Apartment',890.00,340,76.4),
(2024,2,'Seoul','Mapo','Apartment',1185.50,325,72.8),
(2024,2,'Busan','Haeundae','Apartment',705.20,295,78.6),
(2024,3,'Seoul','Jongno','Apartment',1420.00,180,68.5),
(2024,3,'Daegu','Suseong','Apartment',410.30,150,71.2),
(2024,4,'Seoul','Gangnam','Apartment',2085.40,475,84.1),
(2024,4,'Seoul','Yongsan','Apartment',1650.00,220,77.0);

CREATE TABLE IF NOT EXISTS economic_indicator_sample (
  indicator_year INT NOT NULL,
  indicator_month SMALLINT NOT NULL,
  country VARCHAR(30) NOT NULL,
  indicator_code VARCHAR(40) NOT NULL,
  indicator_name VARCHAR(80) NOT NULL,
  indicator_value DECIMAL(14,4) NOT NULL,
  unit VARCHAR(20) NOT NULL
);

DELETE FROM economic_indicator_sample;

INSERT INTO economic_indicator_sample (indicator_year, indicator_month, country, indicator_code, indicator_name, indicator_value, unit) VALUES
(2023,1,'KOR','KR_GDP_YOY','GDP growth (YoY %)',2.6000,'percent'),
(2023,3,'KOR','KR_GDP_YOY','GDP growth (YoY %)',2.9000,'percent'),
(2023,6,'KOR','KR_GDP_YOY','GDP growth (YoY %)',2.8000,'percent'),
(2023,9,'KOR','KR_GDP_YOY','GDP growth (YoY %)',3.1000,'percent'),
(2023,12,'KOR','KR_GDP_YOY','GDP growth (YoY %)',3.4000,'percent'),
(2023,1,'KOR','KR_CPI_YOY','CPI (YoY %)',5.2000,'percent'),
(2023,6,'KOR','KR_CPI_YOY','CPI (YoY %)',3.8000,'percent'),
(2023,12,'KOR','KR_CPI_YOY','CPI (YoY %)',3.2000,'percent'),
(2023,1,'KOR','KR_UNEMP','Unemployment rate',2.9000,'percent'),
(2023,12,'KOR','KR_UNEMP','Unemployment rate',2.7000,'percent'),
(2023,3,'KOR','KR_EXPORT','Exports (USD bn)',155.0000,'bn_usd'),
(2023,12,'KOR','KR_EXPORT','Exports (USD bn)',168.5000,'bn_usd'),
(2024,3,'KOR','KR_GDP_YOY','GDP growth (YoY %)',3.2000,'percent'),
(2024,6,'KOR','KR_GDP_YOY','GDP growth (YoY %)',2.7000,'percent'),
(2024,9,'KOR','KR_GDP_YOY','GDP growth (YoY %)',2.5000,'percent'),
(2024,3,'KOR','KR_CPI_YOY','CPI (YoY %)',3.0000,'percent'),
(2024,9,'KOR','KR_CPI_YOY','CPI (YoY %)',2.1000,'percent'),
(2024,6,'KOR','KR_BASE_RATE','Base rate',3.5000,'percent'),
(2024,12,'KOR','KR_BASE_RATE','Base rate',3.0000,'percent'),
(2024,6,'USA','US_CPI_YOY','CPI (YoY %)',3.3000,'percent'),
(2024,9,'USA','US_CPI_YOY','CPI (YoY %)',2.4000,'percent'),
(2024,6,'USA','US_UNEMP','Unemployment rate',4.1000,'percent');

INSERT INTO dashboard_datasource (user_id, source_name, source_type, config)
SELECT 'admin01', 'demo_realestate', 'jdbc',
  '{"aggregateProvider":true,"password":"","pooled":true,"driver":"org.h2.Driver","jdbcurl":"jdbc:h2:mem:insight-board;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE","username":"sa"}'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_datasource WHERE source_name = 'demo_realestate' AND user_id = 'admin01');

INSERT INTO dashboard_datasource (user_id, source_name, source_type, config)
SELECT 'admin01', 'demo_economic', 'jdbc',
  '{"aggregateProvider":true,"password":"","pooled":true,"driver":"org.h2.Driver","jdbcurl":"jdbc:h2:mem:insight-board;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE","username":"sa"}'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_datasource WHERE source_name = 'demo_economic' AND user_id = 'admin01');

INSERT INTO dashboard_dataset (user_id, category_name, dataset_name, data_json)
SELECT 'admin01', 'Demo Data', 'realestate_korea',
  '{"schema":{"measure":[{"column":"avg_price_100m","id":"m1","type":"column"},{"column":"transaction_count","id":"m2","type":"column"},{"column":"avg_area_sqm","id":"m3","type":"column"}],"dimension":[{"column":"deal_year","id":"d1","type":"column"},{"column":"city","id":"d2","type":"column"},{"column":"district","id":"d3","type":"column"},{"column":"property_type","id":"d4","type":"column"}]},"selects":["deal_year","city","district","property_type","avg_price_100m","transaction_count","avg_area_sqm"],"datasource":1,"query":{"sql":"SELECT deal_year, deal_quarter, city, district, property_type, avg_price_100m, transaction_count, avg_area_sqm FROM real_estate_sample"},"filters":[],"expressions":[]}'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_dataset WHERE dataset_name = 'realestate_korea' AND user_id = 'admin01');

INSERT INTO dashboard_dataset (user_id, category_name, dataset_name, data_json)
SELECT 'admin01', 'Demo Data', 'economic_indicators',
  '{"schema":{"measure":[{"column":"indicator_value","id":"m1","type":"column"}],"dimension":[{"column":"indicator_year","id":"d1","type":"column"},{"column":"indicator_month","id":"d2","type":"column"},{"column":"country","id":"d3","type":"column"},{"column":"indicator_name","id":"d4","type":"column"}]},"selects":["indicator_year","indicator_month","country","indicator_code","indicator_name","indicator_value","unit"],"datasource":1,"query":{"sql":"SELECT indicator_year, indicator_month, country, indicator_code, indicator_name, indicator_value, unit FROM economic_indicator_sample"},"filters":[],"expressions":[]}'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_dataset WHERE dataset_name = 'economic_indicators' AND user_id = 'admin01');

UPDATE dashboard_dataset d
SET data_json = REPLACE(
    REPLACE(d.data_json, '"datasource":1',
      '"datasource":' || (SELECT CAST(ds.datasource_id AS VARCHAR) FROM dashboard_datasource ds WHERE ds.source_name = 'demo_realestate' AND ds.user_id = 'admin01')),
    '"datasetId":1',
    '"datasetId":' || CAST(d.dataset_id AS VARCHAR)
)
WHERE d.dataset_name = 'realestate_korea' AND d.user_id = 'admin01';

UPDATE dashboard_dataset d
SET data_json = REPLACE(
    REPLACE(d.data_json, '"datasource":1',
      '"datasource":' || (SELECT CAST(ds.datasource_id AS VARCHAR) FROM dashboard_datasource ds WHERE ds.source_name = 'demo_economic' AND ds.user_id = 'admin01')),
    '"datasetId":1',
    '"datasetId":' || CAST(d.dataset_id AS VARCHAR)
)
WHERE d.dataset_name = 'economic_indicators' AND d.user_id = 'admin01';

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '2', d.dataset_id, 'write', 'N'
  FROM dashboard_dataset d
 WHERE d.dataset_name IN ('realestate_korea', 'economic_indicators')
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_role_res r
        WHERE r.role_id = '1' AND r.res_type = '2' AND r.res_id = d.dataset_id
   );
