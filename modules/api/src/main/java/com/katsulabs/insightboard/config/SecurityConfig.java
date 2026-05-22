package com.katsulabs.insightboard.config;

import com.katsulabs.insightboard.application.support.JsonMapper;
import com.katsulabs.insightboard.security.SessionAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(InsightBoardSpaProperties.class)
public class SecurityConfig {

    private static final String[] API_PERMIT_ALL = {
        "/api/v1/health",
        "/api/v1/auth/login",
        "/api/v1/auth/session",
        "/api/v1/auth/logout",
        "/actuator/health",
        "/h2-console/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SessionAuthenticationFilter sessionFilter,
            InsightBoardSpaProperties spaProperties)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(API_PERMIT_ALL).permitAll();
                    if (spaProperties.enabled()) {
                        auth.requestMatchers(HttpMethod.GET, "/", "/index.html", "/assets/**", "/insightboard/**")
                                .permitAll()
                                .requestMatchers(SecurityConfig::isSpaDocumentGet)
                                .permitAll()
                                .requestMatchers("/api/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll();
                    } else {
                        auth.anyRequest().authenticated();
                    }
                })
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    JsonMapper.mapper()
                            .writeValue(
                                    response.getOutputStream(),
                                    java.util.Map.of("message", "로그인이 필요합니다."));
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static boolean isSpaDocumentGet(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/api")
                || path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/h2-console")) {
            return false;
        }
        return true;
    }
}
