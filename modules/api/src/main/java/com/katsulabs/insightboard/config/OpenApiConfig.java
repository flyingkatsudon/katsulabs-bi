package com.katsulabs.insightboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI insightBoardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insight Board API")
                        .description("CBoard BDP — Spring Boot 4 REST API (v1)")
                        .version("0.1.0"))
                .addSecurityItem(new SecurityRequirement().addList("sessionCookie"))
                .components(new Components()
                        .addSecuritySchemes(
                                "sessionCookie",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("JSESSIONID")));
    }
}
