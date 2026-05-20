package com.insightboard.web.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "daily_kwd_trend_cnt_minimal_v2")
@Getter
@Setter
public class DailyKwdTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String fid;

    @Column(name = "business_code", length = 50)
    private String businessCode;

    @Column(name = "category_code", length = 20)
    private String categoryCode;

    @Column(name = "doc_date")
    private LocalDate docDate;

    @Column(name = "kwd_a", length = 200)
    private String kwdA;

    @Column(name = "kwd_b", length = 200)
    private String kwdB;

    @Column(name = "doc_cnt_both")
    private Integer docCntBoth;

    @Column(name = "pos_cnt_both")
    private Integer posCntBoth;

    @Column(name = "neg_cnt_both")
    private Integer negCntBoth;
}
