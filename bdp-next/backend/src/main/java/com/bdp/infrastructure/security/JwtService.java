package com.bdp.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String userId, String loginName) {
        return buildToken(userId, loginName, properties.accessExpirationMs());
    }

    public String createRefreshToken(String userId, String loginName) {
        return buildToken(userId, loginName, properties.refreshExpirationMs());
    }

    private String buildToken(String userId, String loginName, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .claim("login", loginName)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
