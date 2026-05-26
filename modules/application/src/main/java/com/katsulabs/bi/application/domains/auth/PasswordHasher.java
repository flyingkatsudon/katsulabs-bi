package com.katsulabs.bi.application.domains.auth;

/**
 * 비밀번호 해시 포트 (레거시 v1 SHA-256 구현은 infrastructure).
 */
public interface PasswordHasher {

    String hash(String plainPassword);
}
