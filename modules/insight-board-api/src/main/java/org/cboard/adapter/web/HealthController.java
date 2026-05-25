package org.cboard.adapter.web;

import java.util.Map;

import org.cboard.application.health.GetHealthUseCase;
import org.cboard.domain.health.HealthSnapshot;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthController {

    private final GetHealthUseCase getHealthUseCase;

    public HealthController(GetHealthUseCase getHealthUseCase) {
        this.getHealthUseCase = getHealthUseCase;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        HealthSnapshot snapshot = getHealthUseCase.execute();
        return Map.of(
                "status", snapshot.status().name(),
                "message", snapshot.message());
    }
}
