package com.bdp.api.report;

import java.time.LocalDate;

public record TrendPointResponse(
        LocalDate docDate, String kwdA, String kwdB, Integer docCntBoth, Integer posCntBoth, Integer negCntBoth) {}
