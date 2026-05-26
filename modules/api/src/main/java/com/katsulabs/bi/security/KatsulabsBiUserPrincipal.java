package com.katsulabs.bi.security;

import com.katsulabs.bi.application.domains.auth.AuthenticatedUser;

public record KatsulabsBiUserPrincipal(AuthenticatedUser user) {
}
