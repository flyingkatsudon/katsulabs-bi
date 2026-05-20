package com.insightboard.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_board")
@Getter
@Setter
public class DashboardBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "board_name", nullable = false, length = 100)
    private String boardName;

    @Column(name = "layout_json", columnDefinition = "CLOB")
    private String layoutJson;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;
}
