package com.katsulabs.bi.application.domains.auth;

public record LoginCommand(String userId, String plainPassword) {}
