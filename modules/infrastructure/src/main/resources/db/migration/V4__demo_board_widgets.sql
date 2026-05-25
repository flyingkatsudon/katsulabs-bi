-- Demo Board: line + pie 위젯 배치 (My Dashboard 에서 그래프 확인용)
-- H2 / PostgreSQL 공통: UPDATE … FROM 미사용 (스칼라 서브쿼리만)

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT
    'admin01',
    'Default Category',
    'Sales by Year',
    '{"config":{"chart_type":"line","keys":[{"type":"column","col":"the_year","alias":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","alias":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
WHERE NOT EXISTS (
    SELECT 1 FROM dashboard_widget WHERE widget_name = 'Sales by Year' AND user_id = 'admin01'
);

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT
    'admin01',
    'Default Category',
    'Sales by Country',
    '{"config":{"chart_type":"pie","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
WHERE NOT EXISTS (
    SELECT 1 FROM dashboard_widget WHERE widget_name = 'Sales by Country' AND user_id = 'admin01'
);

UPDATE dashboard_board
SET layout_json =
    '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
    || '{"widgetId":' || (SELECT CAST(widget_id AS VARCHAR) FROM dashboard_widget WHERE widget_name = 'Sales by Year' AND user_id = 'admin01')
    || ',"name":"Sales by Year","width":6},'
    || '{"widgetId":' || (SELECT CAST(widget_id AS VARCHAR) FROM dashboard_widget WHERE widget_name = 'Sales by Country' AND user_id = 'admin01')
    || ',"name":"Sales by Country","width":6}'
    || ']}]}'
WHERE board_name = 'Demo Board'
  AND user_id = 'admin01'
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'Sales by Year' AND user_id = 'admin01')
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'Sales by Country' AND user_id = 'admin01');
