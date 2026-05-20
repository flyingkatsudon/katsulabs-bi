package com.insightboard.application.report;

import com.insightboard.api.report.TrendPointResponse;
import com.insightboard.api.report.TrendQueryRequest;
import com.insightboard.infrastructure.persistence.DailyKwdTrendRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrendReportService {

    private final DailyKwdTrendRepository trendRepository;

    public TrendReportService(DailyKwdTrendRepository trendRepository) {
        this.trendRepository = trendRepository;
    }

    public List<TrendPointResponse> query(TrendQueryRequest request) {
        return trendRepository
                .search(request.fid(), request.startDate(), request.endDate())
                .stream()
                .map(t -> new TrendPointResponse(
                        t.getDocDate(),
                        t.getKwdA(),
                        t.getKwdB(),
                        t.getDocCntBoth(),
                        t.getPosCntBoth(),
                        t.getNegCntBoth()))
                .toList();
    }
}
