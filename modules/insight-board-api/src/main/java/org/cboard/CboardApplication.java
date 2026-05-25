package org.cboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.cboard.config.CboardProperties;

@SpringBootApplication(scanBasePackages = "org.cboard")
@EnableConfigurationProperties(CboardProperties.class)
public class CboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CboardApplication.class, args);
    }
}
