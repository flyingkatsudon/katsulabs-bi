package com.insightboard.api.application.cboard;

import com.insightboard.api.application.cboard.dto.ServiceStatusDto;
import com.insightboard.api.domain.DashboardJob;
import com.insightboard.api.infrastructure.persistence.DashboardJobRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CboardJobService {

    private final DashboardJobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public CboardJobService(DashboardJobRepository jobRepository, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> getJobList(String userId) {
        return jobRepository.findByUserIdOrderByJobNameAsc(userId).stream()
                .map(this::toView)
                .toList();
    }

    public Map<String, Object> getJobStatus(Long id) {
        return jobRepository.findById(id).map(this::toView).orElse(Map.of());
    }

    public ServiceStatusDto save(String userId, String json) {
        return persist(userId, json, null);
    }

    public ServiceStatusDto update(String userId, String json) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            Long id = Long.valueOf(body.get("id").toString());
            return persist(userId, json, id);
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    public ServiceStatusDto delete(String userId, Long id) {
        jobRepository.deleteById(id);
        return ServiceStatusDto.ok();
    }

    public ServiceStatusDto exec(String userId, Long id) {
        return jobRepository
                .findById(id)
                .map(job -> {
                    job.setLastExecTime(Instant.now());
                    job.setJobStatus(1L);
                    job.setExecLog("Executed at " + Instant.now());
                    jobRepository.save(job);
                    return ServiceStatusDto.ok();
                })
                .orElse(ServiceStatusDto.fail("Job not found"));
    }

    private ServiceStatusDto persist(String userId, String json, Long existingId) {
        try {
            Map<String, Object> body = objectMapper.readValue(json, new TypeReference<>() {});
            DashboardJob job = existingId != null
                    ? jobRepository.findById(existingId).orElseThrow()
                    : new DashboardJob();
            job.setUserId(userId);
            job.setJobName((String) body.get("name"));
            job.setCronExp((String) body.getOrDefault("cronExp", "0 0 * * * ?"));
            job.setJobType((String) body.getOrDefault("jobType", "mail"));
            if (body.get("config") != null) {
                job.setJobConfig(objectMapper.writeValueAsString(body.get("config")));
            }
            job.setJobStatus(0L);
            jobRepository.save(job);
            return ServiceStatusDto.ok();
        } catch (Exception e) {
            return ServiceStatusDto.fail(e.getMessage());
        }
    }

    private Map<String, Object> toView(DashboardJob job) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", job.getJobId());
        m.put("name", job.getJobName());
        m.put("cronExp", job.getCronExp());
        m.put("jobType", job.getJobType());
        m.put("jobStatus", job.getJobStatus());
        m.put("lastExecTime", job.getLastExecTime() != null ? job.getLastExecTime().toString() : null);
        return m;
    }
}
