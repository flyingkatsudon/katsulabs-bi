package com.insightboard.api.report;

import java.time.LocalDate;

public record TrendQueryRequest(String fid, LocalDate startDate, LocalDate endDate) {}
