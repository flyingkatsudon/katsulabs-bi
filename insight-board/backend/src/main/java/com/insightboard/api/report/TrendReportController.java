package com.insightboard.api.report;

import com.insightboard.application.report.TrendReportService;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/report")
public class TrendReportController {

    private final TrendReportService trendReportService;

    public TrendReportController(TrendReportService trendReportService) {
        this.trendReportService = trendReportService;
    }

    /** Legacy: POST /report/get_kwd_trd_v2 */
    @PostMapping("/trends")
    public List<TrendPointResponse> trends(@RequestBody TrendQueryRequest request) {
        return trendReportService.query(request);
    }
}
