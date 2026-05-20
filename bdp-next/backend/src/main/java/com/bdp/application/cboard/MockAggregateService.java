package com.bdp.application.cboard;

import com.bdp.infrastructure.persistence.DailyKwdTrendRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** CBoard getAggregateData 호환 목 응답 (H2 시드 기반). */
@Service
public class MockAggregateService {

    private final DailyKwdTrendRepository trendRepository;

    public MockAggregateService(DailyKwdTrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    public Map<String, Object> aggregate(Long datasetId) {
        var rows = trendRepository.findAll();
        List<Map<String, Object>> columnList = new ArrayList<>();
        columnList.add(column("doc_date", "DATE"));
        columnList.add(column("kwd_a", "STRING"));
        columnList.add(column("doc_cnt_both", "INTEGER"));

        List<List<Object>> data = new ArrayList<>();
        for (var r : rows) {
            data.add(List.of(r.getDocDate(), r.getKwdA(), r.getDocCntBoth()));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("columnList", columnList);
        result.put("data", data);
        result.put("datasetId", datasetId);
        return result;
    }

    private static Map<String, Object> column(String name, String type) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("name", name);
        c.put("columnType", type);
        return c;
    }
}
