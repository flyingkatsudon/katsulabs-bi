package com.katsulabs.bi.infrastructure.persistence.compat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.katsulabs.bi.infrastructure.persistence.mybatis.BoardWidgetRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardLayoutSyncTest {

    @DisplayName("layout_json 에 위젯이 없으면 ib_board_widget 으로 grid layout 을 복원한다")
    @Test
    void composeFromPlacementsPreservesParamRow() {
        String existing =
                """
                {"type":"grid","rows":[{"type":"param","params":[{"col":"the_year"}]}]}
                """;
        BoardWidgetRow w1 = placement(1, 1, "fm_price_line", 1, 0, 6, 320);
        BoardWidgetRow w2 = placement(2, 2, "fm_country_pie", 1, 1, 6, 320);

        String resolved = BoardLayoutSync.resolveLayoutJson(existing, List.of(w1, w2));

        assertTrue(resolved.contains("\"type\":\"param\""));
        assertTrue(resolved.contains("fm_price_line"));
        assertTrue(resolved.contains("fm_country_pie"));
        assertEquals(2, countWidgetIds(resolved));
    }

    @DisplayName("layout_json 에 위젯이 있으면 그대로 반환한다")
    @Test
    void keepsLayoutWhenWidgetsPresent() {
        String layout =
                """
                {"type":"grid","rows":[{"type":"widget","widgets":[{"widgetId":9,"name":"x","width":6}]}]}
                """;
        BoardWidgetRow orphan = placement(9, 9, "x", 0, 0, 6, null);

        String resolved = BoardLayoutSync.resolveLayoutJson(layout, List.of(orphan));

        assertTrue(resolved.contains("\"widgetId\":9"));
        assertEquals(1, countWidgetIds(resolved));
    }

    private static BoardWidgetRow placement(
            long widgetId, int sort, String name, int row, int col, int width, Integer height) {
        BoardWidgetRow r = new BoardWidgetRow();
        r.setWidgetId(widgetId);
        r.setWidgetName(name);
        r.setSortOrder(sort);
        r.setRowIndex(row);
        r.setColumnIndex(col);
        r.setWidthCols(width);
        r.setHeightPx(height);
        return r;
    }

    private static int countWidgetIds(String json) {
        int idx = 0;
        int count = 0;
        while ((idx = json.indexOf("\"widgetId\"", idx)) >= 0) {
            count++;
            idx++;
        }
        return count;
    }
}
