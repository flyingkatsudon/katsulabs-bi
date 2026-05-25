-- FoodMart 샘플 보드에 연도 파라미터 행 추가 (보기 화면 필터 데모)

UPDATE dashboard_board b
SET layout_json = (
    SELECT '{"type":"grid","rows":[{"type":"param","params":[{"col":"the_year","label":"연도","values":["2016","2017"],"defaultValue":"2016"}]},'
        || '{"type":"widget","height":"320","widgets":['
        || '{"widgetId":' || (SELECT CAST(w1.widget_id AS VARCHAR) FROM dashboard_widget w1 WHERE w1.widget_name = 'fm_price_line' AND w1.user_id = 'admin01')
        || ',"name":"fm_price_line","width":6},'
        || '{"widgetId":' || (SELECT CAST(w2.widget_id AS VARCHAR) FROM dashboard_widget w2 WHERE w2.widget_name = 'fm_country_pie' AND w2.user_id = 'admin01')
        || ',"name":"fm_country_pie","width":6}]}]}'
)
WHERE b.board_name = 'FoodMart Sample Dashboard' AND b.user_id = 'admin01'
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_price_line' AND user_id = 'admin01')
  AND EXISTS (SELECT 1 FROM dashboard_widget WHERE widget_name = 'fm_country_pie' AND user_id = 'admin01');
