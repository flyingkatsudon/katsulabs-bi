package com.insightboard.app.application.cboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.insightboard.external.dataprovider.JdbcAggregateQueryBuilder;
import com.insightboard.api.application.cboard.config.AggConfigDto;
import com.insightboard.api.application.cboard.config.DimensionConfigDto;
import com.insightboard.api.application.cboard.config.ValueConfigDto;
import org.junit.jupiter.api.Test;

class JdbcAggregateQueryBuilderTest {

    private final JdbcAggregateQueryBuilder builder = new JdbcAggregateQueryBuilder();

    @Test
    void buildsGroupByWithSum() {
        AggConfigDto cfg = new AggConfigDto();
        DimensionConfigDto row = new DimensionConfigDto();
        row.setColumnName("kwd_a");
        cfg.getRows().add(row);
        ValueConfigDto val = new ValueConfigDto();
        val.setColumn("doc_cnt_both");
        val.setAggType("sum");
        cfg.getValues().add(val);

        var built = builder.build("daily_kwd_trend_cnt_minimal_v2", cfg);
        assertThat(built.sql()).contains("GROUP BY kwd_a");
        assertThat(built.sql()).contains("SUM(doc_cnt_both)");
    }
}
