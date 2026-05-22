-- 데모 DataSource H2 in-memory DB 이름을 katsulabs-bi로 통일 (기존 DB 마이그레이션용)
UPDATE ib_datasource
SET config_json = REPLACE(config_json, 'jdbc:h2:mem:insight-board', 'jdbc:h2:mem:katsulabs-bi')
WHERE config_json LIKE '%jdbc:h2:mem:insight-board%';
