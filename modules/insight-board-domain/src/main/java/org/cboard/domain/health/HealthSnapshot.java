package org.cboard.domain.health;

/**
 * 헬스 스냅샷 — API DTO와 분리된 도메인 모델.
 */
public record HealthSnapshot(HealthStatus status, String message) {

    public static HealthSnapshot up(String message) {
        return new HealthSnapshot(HealthStatus.UP, message);
    }
}
