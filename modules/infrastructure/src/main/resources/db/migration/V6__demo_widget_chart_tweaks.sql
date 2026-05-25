-- Chart Gallery: scatter·themeRiver 차트가 집계 데이터로 그려지도록 설정 보정

UPDATE dashboard_widget
SET data_json = '{"config":{"chart_type":"scatter","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"},{"type":"column","col":"store_cost","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
WHERE user_id = 'admin01' AND widget_name = 'demo_scatter';

UPDATE dashboard_widget
SET data_json = '{"config":{"chart_type":"themeRiver","keys":[{"type":"column","col":"the_year"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
WHERE user_id = 'admin01' AND widget_name = 'demo_theme_river';

UPDATE dashboard_widget
SET data_json = REPLACE(
    REPLACE(data_json, '"datasetId":1', '"datasetId":' || (SELECT CAST(dataset_id AS VARCHAR) FROM dashboard_dataset WHERE dataset_name = 'foodmart_sample')),
    '"datasource":1',
    '"datasource":' || (SELECT CAST(datasource_id AS VARCHAR) FROM dashboard_datasource WHERE source_name = 'demo_source')
)
WHERE widget_name IN ('demo_scatter', 'demo_theme_river')
  AND user_id = 'admin01'
  AND data_json LIKE '%"datasetId":1%';
