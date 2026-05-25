-- 풍부한 FoodMart 샘플 + 차트 유형별 demo 위젯 + Chart Gallery 보드

INSERT INTO sales_fact_sample_flat VALUES
(2016,3,15,TIMESTAMP '2016-03-15 05:00:00','Portland','North West','USA','$30K - $50K',2,'Silver',1,'F',12.5000,5.2000,4.0000),
(2016,6,20,TIMESTAMP '2016-06-20 05:00:00','Seattle','North West','USA','$50K - $70K',3,'Gold',2,'M',18.3000,7.1000,5.0000),
(2017,3,10,TIMESTAMP '2017-03-10 05:00:00','Los Angeles','South West','USA','$70K - $90K',1,'Gold',3,'F',22.1000,9.8000,6.0000),
(2017,9,5,TIMESTAMP '2017-09-05 05:00:00','San Diego','South West','USA','$10K - $30K',4,'Bronze',2,'M',9.6000,4.2000,3.0000),
(2016,12,1,TIMESTAMP '2016-12-01 05:00:00','Mexico City','Mexico Central','Mexico','$30K - $50K',5,'Bronze',1,'F',7.2000,3.1000,2.0000),
(2017,4,18,TIMESTAMP '2017-04-18 05:00:00','Guadalajara','Mexico West','Mexico','$50K - $70K',2,'Silver',2,'M',14.8000,6.5000,4.0000),
(2017,10,22,TIMESTAMP '2017-10-22 05:00:00','Vancouver','Canada West','Canada','$90K - $110K',0,'Gold',1,'F',16.4000,7.0000,5.0000),
(2016,8,30,TIMESTAMP '2016-08-30 05:00:00','Toronto','Canada East','Canada','$110K - $130K',3,'Silver',4,'M',11.9000,5.5000,3.0000),
(2017,2,14,TIMESTAMP '2017-02-14 05:00:00','Boston','North East','USA','$150K +',2,'Gold',2,'F',25.6000,11.2000,7.0000),
(2016,5,25,TIMESTAMP '2016-05-25 05:00:00','New York','North East','USA','$70K - $90K',4,'Normal',3,'M',19.7000,8.9000,6.0000),
(2017,7,7,TIMESTAMP '2017-07-07 05:00:00','Chicago','Mid West','USA','$30K - $50K',1,'Bronze',2,'F',10.5000,4.8000,3.0000),
(2016,10,10,TIMESTAMP '2016-10-10 05:00:00','Detroit','Mid West','USA','$10K - $30K',5,'Normal',1,'M',8.1000,3.6000,2.0000),
(2017,11,11,TIMESTAMP '2017-11-11 05:00:00','Salem','North West','USA','$30K - $50K',2,'Bronze',2,'F',6.3000,2.9000,2.0000),
(2017,1,9,TIMESTAMP '2017-01-09 05:00:00','Seattle','North West','USA','$50K - $70K',1,'Silver',3,'M',13.2000,5.9000,4.0000),
(2016,4,4,TIMESTAMP '2016-04-04 05:00:00','Los Angeles','South West','USA','$30K - $50K',3,'Gold',2,'F',15.0000,6.7000,4.0000);

UPDATE dashboard_dataset
SET data_json = '{"schema":{"measure":[{"column":"store_sales","id":"m1","type":"column"},{"column":"store_cost","id":"m2","type":"column"},{"column":"unit_sales","id":"m3","type":"column"}],"dimension":[{"column":"the_year","id":"d1","type":"column"},{"column":"month_of_year","id":"d2","type":"column"},{"column":"sales_country","id":"d3","type":"column"},{"column":"sales_region","id":"d4","type":"column"},{"column":"gender","id":"d5","type":"column"}]},"selects":["the_year","month_of_year","sales_country","sales_region","gender","store_sales","store_cost","unit_sales"],"datasource":1,"query":{"sql":"SELECT the_year, month_of_year, sales_district, sales_region, sales_country, gender, store_sales, store_cost, unit_sales FROM sales_fact_sample_flat"},"filters":[],"expressions":[]}'
WHERE dataset_name = 'foodmart_sample';

DELETE FROM dashboard_widget WHERE user_id = 'admin01' AND widget_name LIKE 'demo_%';

INSERT INTO dashboard_widget (user_id, category_name, widget_name, data_json) VALUES
('admin01','Chart Gallery','demo_table','{"config":{"chart_type":"table","keys":[{"type":"column","col":"the_year"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_line','{"config":{"chart_type":"line","keys":[{"type":"column","col":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}],"series_type":"line"}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_line_bar','{"config":{"chart_type":"line","keys":[{"type":"column","col":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}],"series_type":"bar"}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_pie','{"config":{"chart_type":"pie","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_kpi','{"config":{"chart_type":"kpi","keys":[],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_scatter','{"config":{"chart_type":"scatter","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"},{"type":"column","col":"store_cost","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_contrast','{"config":{"chart_type":"contrast","keys":[{"type":"column","col":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"},{"type":"column","col":"store_cost","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_funnel','{"config":{"chart_type":"funnel","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_gauge','{"config":{"chart_type":"gauge","keys":[],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_radar','{"config":{"chart_type":"radar","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"},{"type":"column","col":"store_cost","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_sankey','{"config":{"chart_type":"sankey","keys":[{"type":"column","col":"sales_country"}],"groups":[{"type":"column","col":"gender"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_wordcloud','{"config":{"chart_type":"wordCloud","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_treemap','{"config":{"chart_type":"treeMap","keys":[{"type":"column","col":"sales_region"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_sunburst','{"config":{"chart_type":"sunburst","keys":[{"type":"column","col":"sales_region"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_heatmap','{"config":{"chart_type":"heatMapTable","keys":[{"type":"column","col":"the_year"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_boxplot','{"config":{"chart_type":"boxplot","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_parallel','{"config":{"chart_type":"parallel","keys":[{"type":"column","col":"the_year"},{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_pyramid','{"config":{"chart_type":"pyramid","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_pareto','{"config":{"chart_type":"pareto","keys":[{"type":"column","col":"the_year"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}],"series_type":"bar"}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_theme_river','{"config":{"chart_type":"themeRiver","keys":[{"type":"column","col":"the_year"}],"groups":[{"type":"column","col":"sales_country"}],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}'),
('admin01','Chart Gallery','demo_google_map','{"config":{"chart_type":"googleMap","keys":[{"type":"column","col":"sales_country"}],"groups":[],"values":[{"cols":[{"type":"column","col":"store_sales","aggregate_type":"sum"}]}]},"datasetId":1,"datasource":1,"expressions":[],"filterGroups":[]}');

-- H2 / PostgreSQL 공통: UPDATE … FROM 미사용 (V4와 동일)
UPDATE dashboard_widget
SET data_json = REPLACE(
    REPLACE(data_json, '"datasetId":1', '"datasetId":' || (SELECT CAST(dataset_id AS VARCHAR) FROM dashboard_dataset WHERE dataset_name = 'foodmart_sample')),
    '"datasource":1',
    '"datasource":' || (SELECT CAST(datasource_id AS VARCHAR) FROM dashboard_datasource WHERE source_name = 'demo_source')
)
WHERE widget_name LIKE 'demo_%'
  AND user_id = 'admin01';

INSERT INTO dashboard_board (user_id, category_id, board_name, layout_json)
SELECT
    'admin01',
    c.category_id,
    'Chart Gallery',
    '{"type":"grid","rows":[]}'
FROM dashboard_category c
WHERE c.category_name = 'Demo'
  AND NOT EXISTS (SELECT 1 FROM dashboard_board b WHERE b.board_name = 'Chart Gallery' AND b.user_id = 'admin01');

UPDATE dashboard_board b
SET layout_json = (
    SELECT '{"type":"grid","rows":[' || COALESCE(
        (SELECT STRING_AGG(
            '{"type":"widget","height":"260","widgets":[{"widgetId":' || CAST(w.widget_id AS VARCHAR)
            || ',"name":"' || w.widget_name || '","width":6}]}',
            ','
            ORDER BY w.widget_name
        )
         FROM dashboard_widget w
         WHERE w.user_id = 'admin01' AND w.widget_name LIKE 'demo_%'),
        ''
    ) || ']}'
)
WHERE b.board_name = 'Chart Gallery'
  AND b.user_id = 'admin01';
