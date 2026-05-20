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
@Table(name = "dashboard_job")
@Getter
@Setter
public class DashboardJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "cron_exp")
    private String cronExp;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "job_config", columnDefinition = "CLOB")
    private String jobConfig;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "last_exec_time")
    private Instant lastExecTime;

    @Column(name = "job_status")
    private Long jobStatus;

    @Column(name = "exec_log", columnDefinition = "CLOB")
    private String execLog;
}
