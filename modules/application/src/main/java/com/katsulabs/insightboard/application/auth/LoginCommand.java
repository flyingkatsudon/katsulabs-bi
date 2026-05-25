package com.katsulabs.insightboard.application.auth;

public record LoginCommand(String userId, String plainPassword) {}
