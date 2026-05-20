package com.bdp.domain.metadata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboard_widget")
@Getter
@Setter
public class DashboardWidget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "widget_id")
    private Long widgetId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "widget_name")
    private String widgetName;

    @Column(name = "data_json", columnDefinition = "CLOB")
    private String dataJson;
}
