-- RBAC: Super Admin(1), Viewer(2), Manager(3) + 데모 사용자 + DataSource별 샘플 대시보드

UPDATE dashboard_role SET role_name = 'Super Admin' WHERE role_id = '1';

INSERT INTO dashboard_role (role_id, role_name, user_id)
SELECT '2', 'Viewer', 'admin01'
 WHERE NOT EXISTS (SELECT 1 FROM dashboard_role WHERE role_id = '2');

INSERT INTO dashboard_role (role_id, role_name, user_id)
SELECT '3', 'Manager', 'admin01'
 WHERE NOT EXISTS (SELECT 1 FROM dashboard_role WHERE role_id = '3');

INSERT INTO dashboard_user (
    user_id, business_code, login_name, user_name, user_password,
    user_status, rbac_policy, user_state_info, del_cd
)
SELECT 'viewer01', 'SH', 'viewer01', 'Viewer User',
       '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9',
       'active', 0, '0', 'N'
 WHERE NOT EXISTS (SELECT 1 FROM dashboard_user WHERE user_id = 'viewer01' AND business_code = 'SH');

INSERT INTO dashboard_user (
    user_id, business_code, login_name, user_name, user_password,
    user_status, rbac_policy, user_state_info, del_cd
)
SELECT 'manager01', 'SH', 'manager01', 'Manager User',
       '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9',
       'active', 0, '0', 'N'
 WHERE NOT EXISTS (SELECT 1 FROM dashboard_user WHERE user_id = 'manager01' AND business_code = 'SH');

INSERT INTO dashboard_user_role (user_id, business_code, role_id)
SELECT 'viewer01', 'SH', '2'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_user_role WHERE user_id = 'viewer01' AND business_code = 'SH'
 );

INSERT INTO dashboard_user_role (user_id, business_code, role_id)
SELECT 'manager01', 'SH', '3'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_user_role WHERE user_id = 'manager01' AND business_code = 'SH'
 );

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '2', r.res_type, r.res_id, r.permission, 'N'
  FROM dashboard_role_res r
 WHERE r.role_id = '1'
   AND (r.del_cd IS NULL OR UPPER(r.del_cd) != 'X')
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_role_res x
        WHERE x.role_id = '2' AND x.res_type = r.res_type AND x.res_id = r.res_id
   );

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '3', r.res_type, r.res_id, r.permission, 'N'
  FROM dashboard_role_res r
 WHERE r.role_id = '1'
   AND (r.del_cd IS NULL OR UPPER(r.del_cd) != 'X')
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_role_res x
        WHERE x.role_id = '3' AND x.res_type = r.res_type AND x.res_id = r.res_id
   );

INSERT INTO dashboard_category (category_name, user_id)
SELECT 'Data Source Samples', 'admin01'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_category WHERE category_name = 'Data Source Samples' AND user_id = 'admin01'
 );

-- FoodMart 샘플 위젯
INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 'fm_price_line',
       '{"config":{"chart_type":"line","keys":[{"type":"column","col":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_price_line' AND user_id = 'admin01'
 );

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 'fm_country_pie',
       '{"config":{"chart_type":"pie","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_country_pie' AND user_id = 'admin01'
 );

-- 부동산 샘플 위젯
INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 're_price_line',
       '{"config":{"chart_type":"line","keys":[{"type":"column","col":"deal_year"}],"groups":[{"type":"column","col":"city"}],"values":[{"cols":[{"type":"column","col":"avg_price_100m","aggregate_type":"avg"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 're_price_line' AND user_id = 'admin01'
 );

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 're_tx_bar',
       '{"config":{"chart_type":"line","keys":[{"type":"column","col":"city"}],"groups":[],"values":[{"cols":[{"type":"column","col":"transaction_count","aggregate_type":"sum"}],"series_type":"bar"}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 're_tx_bar' AND user_id = 'admin01'
 );

-- 경제 지표 샘플 위젯
INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 'eco_indicator_line',
       '{"config":{"chart_type":"line","keys":[{"type":"column","col":"indicator_year"}],"groups":[{"type":"column","col":"indicator_name"}],"values":[{"cols":[{"type":"column","col":"indicator_value","aggregate_type":"avg"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 'eco_indicator_line' AND user_id = 'admin01'
 );

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json)
SELECT 'admin01', 'Data Source Samples', 'eco_country_table',
       '{"config":{"chart_type":"table","keys":[{"type":"column","col":"country"}],"groups":[{"type":"column","col":"indicator_name"}],"values":[{"cols":[{"type":"column","col":"indicator_value","aggregate_type":"avg"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'
 WHERE NOT EXISTS (
     SELECT 1 FROM dashboard_widget WHERE widget_name = 'eco_country_table' AND user_id = 'admin01'
 );

UPDATE dashboard_widget w
SET data_json = REPLACE(
    REPLACE(w.data_json, '"datasetId":1',
        '"datasetId":' || (SELECT CAST(d.dataset_id AS VARCHAR) FROM dashboard_dataset d WHERE d.dataset_name = 'foodmart_sample')),
    '"datasource":1',
    '"datasource":' || (SELECT CAST(ds.datasource_id AS VARCHAR) FROM dashboard_datasource ds WHERE ds.source_name = 'demo_source' AND ds.user_id = 'admin01')
)
WHERE w.widget_name IN ('fm_price_line', 'fm_country_pie') AND w.user_id = 'admin01';

UPDATE dashboard_widget w
SET data_json = REPLACE(
    REPLACE(w.data_json, '"datasetId":1',
        '"datasetId":' || (SELECT CAST(d.dataset_id AS VARCHAR) FROM dashboard_dataset d WHERE d.dataset_name = 'realestate_korea')),
    '"datasource":1',
    '"datasource":' || (SELECT CAST(ds.datasource_id AS VARCHAR) FROM dashboard_datasource ds WHERE ds.source_name = 'demo_realestate' AND ds.user_id = 'admin01')
)
WHERE w.widget_name IN ('re_price_line', 're_tx_bar') AND w.user_id = 'admin01';

UPDATE dashboard_widget w
SET data_json = REPLACE(
    REPLACE(w.data_json, '"datasetId":1',
        '"datasetId":' || (SELECT CAST(d.dataset_id AS VARCHAR) FROM dashboard_dataset d WHERE d.dataset_name = 'economic_indicators')),
    '"datasource":1',
    '"datasource":' || (SELECT CAST(ds.datasource_id AS VARCHAR) FROM dashboard_datasource ds WHERE ds.source_name = 'demo_economic' AND ds.user_id = 'admin01')
)
WHERE w.widget_name IN ('eco_indicator_line', 'eco_country_table') AND w.user_id = 'admin01';

INSERT INTO dashboard_board (user_id, category_id, board_name, layout_json)
SELECT 'admin01', c.category_id, 'FoodMart Sample Dashboard',
       '{"type":"grid","rows":[]}'
  FROM dashboard_category c
 WHERE c.category_name = 'Data Source Samples' AND c.user_id = 'admin01'
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_board b WHERE b.board_name = 'FoodMart Sample Dashboard' AND b.user_id = 'admin01'
   );

INSERT INTO dashboard_board (user_id, category_id, board_name, layout_json)
SELECT 'admin01', c.category_id, 'Real Estate Sample Dashboard',
       '{"type":"grid","rows":[]}'
  FROM dashboard_category c
 WHERE c.category_name = 'Data Source Samples' AND c.user_id = 'admin01'
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_board b WHERE b.board_name = 'Real Estate Sample Dashboard' AND b.user_id = 'admin01'
   );

INSERT INTO dashboard_board (user_id, category_id, board_name, layout_json)
SELECT 'admin01', c.category_id, 'Economic Indicators Sample Dashboard',
       '{"type":"grid","rows":[]}'
  FROM dashboard_category c
 WHERE c.category_name = 'Data Source Samples' AND c.user_id = 'admin01'
   AND NOT EXISTS (
       SELECT 1 FROM dashboard_board b WHERE b.board_name = 'Economic Indicators Sample Dashboard' AND b.user_id = 'admin01'
   );

UPDATE dashboard_board b
SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM dashboard_widget w1 WHERE w1.widget_name = 'fm_price_line' AND w1.user_id = 'admin01')
        || ',"name":"fm_price_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM dashboard_widget w2 WHERE w2.widget_name = 'fm_country_pie' AND w2.user_id = 'admin01')
        || ',"name":"fm_country_pie","width":6}]}]}'
)
WHERE b.board_name = 'FoodMart Sample Dashboard' AND b.user_id = 'admin01'
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_price_line' AND user_id = 'admin01')
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_country_pie' AND user_id = 'admin01');

UPDATE dashboard_board b
SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM dashboard_widget w1 WHERE w1.widget_name = 're_price_line' AND w1.user_id = 'admin01')
        || ',"name":"re_price_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM dashboard_widget w2 WHERE w2.widget_name = 're_tx_bar' AND w2.user_id = 'admin01')
        || ',"name":"re_tx_bar","width":6}]}]}'
)
WHERE b.board_name = 'Real Estate Sample Dashboard' AND b.user_id = 'admin01'
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 're_price_line' AND user_id = 'admin01')
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 're_tx_bar' AND user_id = 'admin01');

UPDATE dashboard_board b
SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM dashboard_widget w1 WHERE w1.widget_name = 'eco_indicator_line' AND w1.user_id = 'admin01')
        || ',"name":"eco_indicator_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM dashboard_widget w2 WHERE w2.widget_name = 'eco_country_table' AND w2.user_id = 'admin01')
        || ',"name":"eco_country_table","width":6}]}]}'
)
WHERE b.board_name = 'Economic Indicators Sample Dashboard' AND b.user_id = 'admin01'
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'eco_indicator_line' AND user_id = 'admin01')
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'eco_country_table' AND user_id = 'admin01');
