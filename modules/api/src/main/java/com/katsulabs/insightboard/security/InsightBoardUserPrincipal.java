package com.katsulabs.insightboard.security;

import com.katsulabs.insightboard.application.auth.AuthenticatedUser;

public record InsightBoardUserPrincipal(AuthenticatedUser user) {
}
