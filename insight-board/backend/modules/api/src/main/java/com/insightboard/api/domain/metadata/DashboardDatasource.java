package com.insightboard.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_datasource")
@Getter
@Setter
public class DashboardDatasource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "config", columnDefinition = "CLOB")
    private String config;
}
