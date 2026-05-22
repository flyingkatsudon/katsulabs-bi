package com.katsulabs.bi.application.health;

import com.katsulabs.bi.domain.health.HealthSnapshot;

/**
 * 헬스 조회 유스케이스 — 인프라 없이 동작하는 최소 TDD 대상.
 */
public class GetHealthUseCase {

    public HealthSnapshot execute() {
        return HealthSnapshot.up("katsulabs-bi");
    }
}
