package com.katsulabs.insightboard.application.common;

/**
 * 레거시 {@code ServiceStatus} 호환 응답 (status: 1=성공, 2=실패).
 */
public record ServiceResult(String status, String message, Long id) {

    public static ServiceResult success(String message) {
        return new ServiceResult("1", message, null);
    }

    public static ServiceResult success(String message, Long id) {
        return new ServiceResult("1", message, id);
    }

    public static ServiceResult fail(String message) {
        return new ServiceResult("2", message, null);
    }
}
