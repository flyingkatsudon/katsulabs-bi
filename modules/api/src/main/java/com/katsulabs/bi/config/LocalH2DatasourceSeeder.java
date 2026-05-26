package com.katsulabs.bi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.katsulabs.bi.application.common.JsonMapper;
import com.katsulabs.bi.domain.domains.datasource.DatasourceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * local-h2 (in-memory H2) 재시작 시 {@code local-h2-datasources.json} 에 정의된 JDBC 데이터소스를 자동 등록합니다.
 */
@Component
@Profile("local-h2")
@RequiredArgsConstructor
@Slf4j
public class LocalH2DatasourceSeeder implements ApplicationRunner {

    private static final String SEED_RESOURCE = "local-h2-datasources.json";
    private static final TypeReference<List<LocalH2DatasourceSeed>> SEED_TYPE = new TypeReference<>() {};

    private final DatasourceRepository datasourceRepository;


    @Override
    public void run(ApplicationArguments args) {
        ClassPathResource resource = new ClassPathResource(SEED_RESOURCE);
        if (!resource.exists()) {
            log.debug("No {} on classpath; skipping local datasource seed", SEED_RESOURCE);
            return;
        }

        List<LocalH2DatasourceSeed> seeds;
        try (InputStream in = resource.getInputStream()) {
            seeds = JsonMapper.mapper().readValue(in, SEED_TYPE);
        } catch (Exception e) {
            log.warn("Failed to read {}: {}", SEED_RESOURCE, e.getMessage());
            return;
        }

        for (LocalH2DatasourceSeed seed : seeds) {
            if (seed.name() == null || seed.name().isBlank()) {
                continue;
            }
            if (seed.type() == null || seed.type().isBlank()) {
                log.warn("Skipping datasource seed with empty type: {}", seed.name());
                continue;
            }
            if (seed.configJson() == null || seed.configJson().isBlank()) {
                log.warn("Skipping datasource seed with empty configJson: {}", seed.name());
                continue;
            }

            String ownerUserId = seed.ownerUserId() == null || seed.ownerUserId().isBlank()
                    ? "admin01"
                    : seed.ownerUserId();

            var existing = datasourceRepository.findAllSummaries().stream()
                    .filter(d -> seed.name().equals(d.name()))
                    .findFirst();

            if (existing.isEmpty()) {
                datasourceRepository.insert(ownerUserId, seed.name(), seed.type(), seed.configJson());
                log.info("Seeded local-h2 datasource: {}", seed.name());
            } else {
                datasourceRepository.update(
                        existing.get().id(), seed.name(), seed.type(), seed.configJson());
                log.debug("Updated local-h2 datasource seed: {}", seed.name());
            }
        }
    }

    private record LocalH2DatasourceSeed(String name, String type, String ownerUserId, String configJson) {}
}
