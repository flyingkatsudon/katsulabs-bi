package com.insightboard;

import com.insightboard.infrastructure.config.CorsProperties;
import com.insightboard.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class InsightBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightBoardApplication.class, args);
    }
}
