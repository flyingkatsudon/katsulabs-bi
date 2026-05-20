package com.insightboard.app;

import com.insightboard.app.config.CorsProperties;
import com.insightboard.app.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.insightboard")
@EnableJpaRepositories(
        basePackages = {
            "com.insightboard.api.infrastructure.persistence",
            "com.insightboard.web.infrastructure.persistence"
        })
@EntityScan(basePackages = {"com.insightboard.api.domain", "com.insightboard.web.domain"})
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class InsightBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightBoardApplication.class, args);
    }
}
