-- 부동산·경제 지표 샘플: V8 dashboard_* 위젯/보드 → ib_* (V11 에서 보드만 빈 채로 생성됨)

-- ─── 부동산 위젯 (realestate_korea) ───
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Data Source Samples' AND owner_user_id = 'admin01'),
       're_price_line', 'admin01', 'line'
  FROM ib_dataset d WHERE d.name = 'realestate_korea'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 're_price_line');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'deal_year', 'deal_year', NULL, 1
  FROM ib_widget w WHERE w.name = 're_price_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'city', 'city', NULL, 2
  FROM ib_widget w WHERE w.name = 're_price_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'avg_price_100m', 'avg_price_100m', 'avg', 3
  FROM ib_widget w WHERE w.name = 're_price_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 3);

INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type, options_json)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Data Source Samples' AND owner_user_id = 'admin01'),
       're_tx_bar', 'admin01', 'line', '{"series_type":"bar"}'
  FROM ib_dataset d WHERE d.name = 'realestate_korea'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 're_tx_bar');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'city', 'city', NULL, 1
  FROM ib_widget w WHERE w.name = 're_tx_bar'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'transaction_count', 'transaction_count', 'sum', 2
  FROM ib_widget w WHERE w.name = 're_tx_bar'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

-- ─── 경제 지표 위젯 (economic_indicators) ───
INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Data Source Samples' AND owner_user_id = 'admin01'),
       'eco_indicator_line', 'admin01', 'line'
  FROM ib_dataset d WHERE d.name = 'economic_indicators'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'eco_indicator_line');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'indicator_year', 'indicator_year', NULL, 1
  FROM ib_widget w WHERE w.name = 'eco_indicator_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'indicator_name', 'indicator_name', NULL, 2
  FROM ib_widget w WHERE w.name = 'eco_indicator_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'indicator_value', 'indicator_value', 'avg', 3
  FROM ib_widget w WHERE w.name = 'eco_indicator_line'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 3);

INSERT INTO ib_widget (dataset_id, category_id, name, owner_user_id, chart_type)
SELECT d.dataset_id,
       (SELECT category_id FROM ib_category WHERE name = 'Data Source Samples' AND owner_user_id = 'admin01'),
       'eco_country_table', 'admin01', 'table'
  FROM ib_dataset d WHERE d.name = 'economic_indicators'
   AND NOT EXISTS (SELECT 1 FROM ib_widget WHERE name = 'eco_country_table');

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'ROW', 'country', 'country', NULL, 1
  FROM ib_widget w WHERE w.name = 'eco_country_table'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 1);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'COLUMN', 'indicator_name', 'indicator_name', NULL, 2
  FROM ib_widget w WHERE w.name = 'eco_country_table'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 2);

INSERT INTO ib_widget_binding (widget_id, axis, column_name, alias, aggregate_fn, sort_order)
SELECT w.widget_id, 'VALUE', 'indicator_value', 'indicator_value', 'avg', 3
  FROM ib_widget w WHERE w.name = 'eco_country_table'
   AND NOT EXISTS (SELECT 1 FROM ib_widget_binding b WHERE b.widget_id = w.widget_id AND b.sort_order = 3);

-- ─── Real Estate Sample Dashboard ───
UPDATE ib_board b
   SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM ib_widget w1 WHERE w1.name = 're_price_line')
        || ',"name":"re_price_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM ib_widget w2 WHERE w2.name = 're_tx_bar')
        || ',"name":"re_tx_bar","width":6}]}]}'
   ),
       published_to_viewers = TRUE,
       published_by = 'admin01',
       published_at = CURRENT_TIMESTAMP
 WHERE b.name = 'Real Estate Sample Dashboard' AND b.owner_user_id = 'admin01'
   AND EXISTS (SELECT 1 FROM ib_widget WHERE name = 're_price_line')
   AND EXISTS (SELECT 1 FROM ib_widget WHERE name = 're_tx_bar');

DELETE FROM ib_board_widget
 WHERE board_id = (SELECT board_id FROM ib_board WHERE name = 'Real Estate Sample Dashboard' AND owner_user_id = 'admin01');

INSERT INTO ib_board_widget (board_id, widget_id, row_index, column_index, width_cols, height_px, sort_order)
SELECT b.board_id, w.widget_id, 0,
       CASE w.name WHEN 're_price_line' THEN 0 ELSE 1 END,
       6, 320,
       CASE w.name WHEN 're_price_line' THEN 1 ELSE 2 END
  FROM ib_board b
  JOIN ib_widget w ON w.name IN ('re_price_line', 're_tx_bar')
 WHERE b.name = 'Real Estate Sample Dashboard' AND b.owner_user_id = 'admin01';

-- ─── Economic Indicators Sample Dashboard ───
UPDATE ib_board b
   SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM ib_widget w1 WHERE w1.name = 'eco_indicator_line')
        || ',"name":"eco_indicator_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM ib_widget w2 WHERE w2.name = 'eco_country_table')
        || ',"name":"eco_country_table","width":6}]}]}'
   ),
       published_to_viewers = TRUE,
       published_by = 'admin01',
       published_at = CURRENT_TIMESTAMP
 WHERE b.name = 'Economic Indicators Sample Dashboard' AND b.owner_user_id = 'admin01'
   AND EXISTS (SELECT 1 FROM ib_widget WHERE name = 'eco_indicator_line')
   AND EXISTS (SELECT 1 FROM ib_widget WHERE name = 'eco_country_table');

DELETE FROM ib_board_widget
 WHERE board_id = (SELECT board_id FROM ib_board WHERE name = 'Economic Indicators Sample Dashboard' AND owner_user_id = 'admin01');

INSERT INTO ib_board_widget (board_id, widget_id, row_index, column_index, width_cols, height_px, sort_order)
SELECT b.board_id, w.widget_id, 0,
       CASE w.name WHEN 'eco_indicator_line' THEN 0 ELSE 1 END,
       6, 320,
       CASE w.name WHEN 'eco_indicator_line' THEN 1 ELSE 2 END
  FROM ib_board b
  JOIN ib_widget w ON w.name IN ('eco_indicator_line', 'eco_country_table')
 WHERE b.name = 'Economic Indicators Sample Dashboard' AND b.owner_user_id = 'admin01';
