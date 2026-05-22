package com.katsulabs.bi.application.auth;

public record LoginCommand(String userId, String plainPassword) {}
