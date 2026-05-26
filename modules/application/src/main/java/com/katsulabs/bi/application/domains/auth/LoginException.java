package com.katsulabs.bi.application.domains.auth;

public class LoginException extends RuntimeException {

    public LoginException(String message) {
        super(message);
    }
}
