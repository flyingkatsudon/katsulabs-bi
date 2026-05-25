package org.cboard.application.auth;

public record LoginCommand(String businessCode, String userId, String plainPassword) {
}
