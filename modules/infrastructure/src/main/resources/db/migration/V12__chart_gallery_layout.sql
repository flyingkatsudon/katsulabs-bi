-- Chart Gallery: demo 위젯 + layout_json (보기 화면은 layout_json 기준)

INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_pie', 'admin01', 'pie'
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_pie');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_pie'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 2
  FROM ib_widget w WHERE w.name = 'demo_pie'
   AND EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1)
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_table', 'admin01', 'table'
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_table');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'the_year', 'the_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'demo_table'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'SALES_COUNTRY', 'SALES_COUNTRY', NULL, 2
  FROM ib_widget w WHERE w.name = 'demo_table'
   AND EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1)
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 3
  FROM ib_widget w WHERE w.name = 'demo_table'
   AND EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2)
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 3);

INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Demo' AND owner_user_id = 'admin01'),
       'demo_kpi', 'admin01', 'kpi'
  FROM ib_dataset d WHERE d.name = 'foodmart_sample'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'demo_kpi');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'store_sales', 'store_sales', 'sum', 1
  FROM ib_widget w WHERE w.name = 'demo_kpi'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id);

UPDATE ib_board b
   SET layout_json = (
    SELECT '{"type":"grid","rows":[' || COALESCE(
        (SELECT STRING_AGG(
            '{"type":"widget","height":"280","widgets":[{"widgetId":' || CAST(w.widget_id AS VARCHAR)
            || ',"name":"' || w.name || '","width":6}]}',
            ','
            ORDER BY w.name
        )
           FROM ib_widget w
          WHERE w.owner_user_id = 'admin01'
            AND w.name IN ('demo_kpi', 'demo_line', 'demo_pie', 'demo_table')),
        ''
    ) || ']}'
   )
 WHERE b.name = 'Chart Gallery' AND b.owner_user_id = 'admin01';
