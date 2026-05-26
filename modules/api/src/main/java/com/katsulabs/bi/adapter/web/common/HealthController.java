package com.katsulabs.bi.adapter.web.common;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import com.katsulabs.bi.application.common.health.GetHealthUseCase;
import com.katsulabs.bi.domain.common.health.HealthSnapshot;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HealthController {

    private final GetHealthUseCase getHealthUseCase;


    @GetMapping("/health")
    public Map<String, String> health() {
        HealthSnapshot snapshot = getHealthUseCase.execute();
        return Map.of(
                "status", snapshot.status().name(),
                "message", snapshot.message());
    }
}
