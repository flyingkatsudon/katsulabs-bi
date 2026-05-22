package com.katsulabs.bi.security;

import com.katsulabs.bi.application.auth.AuthenticatedUser;

public record KatsulabsBiUserPrincipal(AuthenticatedUser user) {
}
