package com.bdp.infrastructure.persistence;

import com.bdp.domain.analytics.DailyKwdTrend;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyKwdTrendRepository extends JpaRepository<DailyKwdTrend, Long> {

    @Query("""
            SELECT t FROM DailyKwdTrend t
            WHERE (:fid IS NULL OR t.fid = :fid)
              AND (:start IS NULL OR t.docDate >= :start)
              AND (:end IS NULL OR t.docDate <= :end)
            ORDER BY t.docDate DESC, t.docCntBoth DESC
            """)
    List<DailyKwdTrend> search(
            @Param("fid") String fid,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
