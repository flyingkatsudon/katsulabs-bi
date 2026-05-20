package com.bdp;

import com.bdp.infrastructure.config.CorsProperties;
import com.bdp.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class BdpApplication {

    public static void main(String[] args) {
        SpringApplication.run(BdpApplication.class, args);
    }
}
