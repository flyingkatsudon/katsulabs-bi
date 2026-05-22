package com.katsulabs.bi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.katsulabs.bi")
public class KatsulabsBiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KatsulabsBiApplication.class, args);
    }
}
