package com.katsulabs.insightboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.katsulabs.insightboard")
public class InsightBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightBoardApplication.class, args);
    }
}
