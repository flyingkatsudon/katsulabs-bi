package com.katsulabs.bi.infrastructure.domains.board.persistence.compat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardFilterMapper;
import com.katsulabs.bi.infrastructure.domains.board.persistence.mybatis.BoardFilterRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class BoardFilterSyncTest {

    @Test
    @DisplayName("layout_json 에 param 이 없으면 ib_board_filter 로 param row 를 복원한다")
    void injectsParamRowFromFilters() {
        BoardFilterRow filter = new BoardFilterRow();
        filter.setColumnName("the_year");
        filter.setLabel("연도");
        filter.setDefaultValue("2016");
        filter.setOptionValues(List.of("2016", "2017"));

        String layout =
                """
                {"type":"grid","rows":[{"type":"widget","widgets":[{"widgetId":1,"name":"w","width":6}]}]}
                """;
        String resolved = BoardFilterSync.resolveLayoutJson(layout, List.of(filter));

        assertTrue(resolved.contains("\"type\":\"param\""));
        assertTrue(resolved.contains("\"col\":\"the_year\""));
        assertTrue(resolved.contains("\"defaultValue\":\"2016\""));
        assertTrue(resolved.contains("2017"));
    }

    @Test
    @DisplayName("layout_json 저장 시 ib_board_filter 를 갱신한다")
    void syncsFiltersFromParamRows() {
        BoardFilterMapper mapper = mock(BoardFilterMapper.class);
        String layout =
                """
                {"type":"grid","rows":[{"type":"param","params":[{"col":"the_year","label":"연도","values":["2016","2017"],"defaultValue":"2016"}]}]}
                """;

        doAnswer(inv -> {
                    BoardFilterRow row = inv.getArgument(0);
                    row.setFilterId(99L);
                    return null;
                })
                .when(mapper)
                .insert(any(BoardFilterRow.class));

        BoardFilterSync.syncFromLayoutJson(mapper, 5L, layout);

        InOrder order = inOrder(mapper);
        order.verify(mapper).deleteOptionsByBoardId(5L);
        order.verify(mapper).deleteByBoardId(5L);
        verify(mapper).insert(any(BoardFilterRow.class));
        verify(mapper).insertOption(any(Long.class), eq("2016"));
        verify(mapper).insertOption(any(Long.class), eq("2017"));
    }
}
