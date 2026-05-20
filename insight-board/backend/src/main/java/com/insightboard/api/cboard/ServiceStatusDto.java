package com.insightboard.api.cboard;

public record ServiceStatusDto(String status, String msg) {

    public static ServiceStatusDto ok() {
        return new ServiceStatusDto("1", "");
    }

    public static ServiceStatusDto fail(String msg) {
        return new ServiceStatusDto("0", msg);
    }
}
