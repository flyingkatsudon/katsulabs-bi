package com.katsulabs.bi.infrastructure.domains.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.katsulabs.bi.application.domains.auth.PasswordHasher;
import org.springframework.stereotype.Component;

/**
 * 레거시 CBoard: SHA-256(plain), HEX 대문자 (Spring ShaPasswordEncoder 256 호환).
 */
@Component
public class LegacySha256PasswordHasher implements PasswordHasher {

    @Override
    public String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
