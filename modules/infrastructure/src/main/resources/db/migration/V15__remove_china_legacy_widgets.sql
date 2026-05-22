-- 중국 지도(Baidu)·미사용 googleMap 데모 위젯 제거 및 Chart Gallery 레이아웃 갱신

DELETE FROM ib_board_widget
 WHERE widget_id IN (SELECT widget_id FROM ib_widget WHERE name = 'demo_google_map');

DELETE FROM ib_widget_binding
 WHERE widget_id IN (SELECT widget_id FROM ib_widget WHERE name = 'demo_google_map');

DELETE FROM ib_widget WHERE name = 'demo_google_map';

DELETE FROM ib_board_widget
 WHERE widget_id IN (SELECT widget_id FROM ib_widget WHERE chart_type IN ('chinaMap', 'chinaMapBmap'));

DELETE FROM ib_widget_binding
 WHERE widget_id IN (SELECT widget_id FROM ib_widget WHERE chart_type IN ('chinaMap', 'chinaMapBmap'));

DELETE FROM ib_widget WHERE chart_type IN ('chinaMap', 'chinaMapBmap');

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
