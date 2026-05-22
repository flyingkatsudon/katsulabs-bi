package com.katsulabs.bi.application.auth;

/**
 * 비밀번호 해시 포트 (레거시 CBoard SHA-256 구현은 infrastructure).
 */
public interface PasswordHasher {

    String hash(String plainPassword);
}
