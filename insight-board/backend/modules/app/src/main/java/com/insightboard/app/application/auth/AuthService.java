package com.insightboard.app.application.auth;

import com.insightboard.app.presentation.auth.LoginRequest;
import com.insightboard.app.presentation.auth.TokenResponse;
import com.insightboard.api.infrastructure.persistence.DashboardUserRepository;
import com.insightboard.app.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final DashboardUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            DashboardUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(LoginRequest request) {
        var user = userRepository
                .findByLoginName(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getUserPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        return new TokenResponse(
                jwtService.createAccessToken(user.getUserId(), user.getLoginName()),
                jwtService.createRefreshToken(user.getUserId(), user.getLoginName()),
                "Bearer");
    }
}
