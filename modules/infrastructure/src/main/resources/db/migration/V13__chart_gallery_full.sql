-- Chart Gallery: V5 수준 demo_* 위젯 + layout/placement 갱신

-- foodmart_sample 추가 컬럼 메타
INSERT INTO ib_dataset_column (dataset_id, column_name, kind, alias, sort_order)
SELECT d.dataset_id, 'month_of_year', 'DIMENSION', 'month_of_year', 5
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (
       SELECT 1 FROM ib_dataset_column c
        WHERE c.dataset_id = d.dataset_id AND c.column_name = 'month_of_year'
   );

INSERT INTO ib_dataset_column (dataset_id, column_name, kind, alias, sort_order)
SELECT d.dataset_id, 'SALES_REGION', 'DIMENSION', 'SALES_REGION', 6
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (
       SELECT 1 FROM ib_dataset_column c
        WHERE c.dataset_id = d.dataset_id AND c.column_name = 'SALES_REGION'
   );

INSERT INTO ib_dataset_column (dataset_id, column_name, kind, alias, sort_order)
SELECT d.dataset_id, 'gender', 'DIMENSION', 'gender', 7
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (
       SELECT 1 FROM ib_dataset_column c
        WHERE c.dataset_id = d.dataset_id AND c.column_name = 'gender'
   );

INSERT INTO ib_dataset_column (dataset_id, column_name, kind, alias, sort_order)
SELECT d.dataset_id, 'unit_sales', 'MEASURE', 'unit_sales', 8
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (
       SELECT 1 FROM ib_dataset_column c
        WHERE c.dataset_id = d.dataset_id AND c.column_name = 'unit_sales'
   );


-- demo_line_bar
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_line_bar', 'admin01', 'line', '{"series_type":"bar"}'
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_line_bar');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_line_bar'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_line_bar'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_scatter
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_scatter', 'admin01', 'scatter', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_scatter');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_scatter'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_scatter'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_cost', 'store_cost', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_scatter'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_contrast
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_contrast', 'admin01', 'contrast', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_contrast');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_contrast'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_contrast'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_cost', 'store_cost', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_contrast'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_funnel
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_funnel', 'admin01', 'funnel', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_funnel');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_funnel'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_funnel'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_gauge
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_gauge', 'admin01', 'gauge', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_gauge');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 1
  FROM ib_widget w WHERE w.name = 'demo_gauge'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );


-- demo_radar
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_radar', 'admin01', 'radar', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_radar');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_radar'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_radar'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_cost', 'store_cost', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_radar'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_sankey
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_sankey', 'admin01', 'sankey', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_sankey');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_sankey'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'gender', 'gender', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_sankey'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_sankey'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_wordcloud
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_wordcloud', 'admin01', 'wordCloud', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_wordcloud');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_wordcloud'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_wordcloud'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_treemap
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_treemap', 'admin01', 'treeMap', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_treemap');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_REGION', 'SALES_REGION', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_treemap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_treemap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_treemap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_sunburst
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_sunburst', 'admin01', 'sunburst', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_sunburst');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_REGION', 'SALES_REGION', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_sunburst'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_sunburst'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_sunburst'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_heatmap
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_heatmap', 'admin01', 'heatMapTable', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_heatmap');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_heatmap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_heatmap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_heatmap'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_boxplot
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_boxplot', 'admin01', 'boxplot', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_boxplot');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_boxplot'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_boxplot'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_parallel
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_parallel', 'admin01', 'parallel', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_parallel');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_parallel'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_parallel'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_parallel'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_pyramid
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_pyramid', 'admin01', 'pyramid', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_pyramid');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_pyramid'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_pyramid'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_pareto
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_pareto', 'admin01', 'pareto', '{"series_type":"bar"}'
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_pareto');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_pareto'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_pareto'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


-- demo_theme_river
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_theme_river', 'admin01', 'themeRiver', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_theme_river');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_theme_river'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_theme_river'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_theme_river'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 3
   );


-- demo_google_map
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_google_map', 'admin01', 'googleMap', NULL
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_google_map');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_google_map'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 1
   );

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_google_map'
   AND NOT EXISTS (
       SELECT 1 FROM ib_widget_binding x
        WHERE x.widget_id = w.widget_id AND x.sort_order = 2
   );


UPDATE ib_board b
   SET layout_json = (
    SELECT '{"type":"grid","rows":[' || COALESCE(
        (SELECT STRING_AGG(
            '{"type":"widget","height":"260","widgets":[{"widgetId":' || CAST(w.widget_id AS VARCHAR)
            || ',"name":"' || w.name || '","width":6}]}',
            ','
            ORDER BY w.name
        )
           FROM ib_widget w
          WHERE w.owner_user_id = 'admin01'
            AND w.name LIKE 'demo_%'),
        ''
    ) || ']}'
   )
 WHERE b.name = 'Chart Gallery' AND b.owner_user_id = 'admin01';

DELETE FROM ib_board_widget
 WHERE board_id = (SELECT board_id FROM ib_board WHERE name = 'Chart Gallery' AND owner_user_id = 'admin01');

INSERT INTO ib_board_widget (board_id, widget_id, row_index, column_index, width_cols, height_px, sort_order)
SELECT b.board_id,
       w.widget_id,
       (SELECT COUNT(*) FROM ib_widget w2 WHERE w2.owner_user_id = 'admin01' AND w2.name LIKE 'demo_%' AND w2.name < w.name),
       0,
       6,
       260,
       (SELECT COUNT(*) FROM ib_widget w2 WHERE w2.owner_user_id = 'admin01' AND w2.name LIKE 'demo_%' AND w2.name <= w.name)
  FROM ib_board b
  JOIN ib_widget w ON w.owner_user_id = 'admin01' AND w.name LIKE 'demo_%'
 WHERE b.name = 'Chart Gallery' AND b.owner_user_id = 'admin01';

