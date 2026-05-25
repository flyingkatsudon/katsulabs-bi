package org.cboard.security;

import org.cboard.application.auth.AuthenticatedUser;

public record CboardUserPrincipal(AuthenticatedUser user) {
}
