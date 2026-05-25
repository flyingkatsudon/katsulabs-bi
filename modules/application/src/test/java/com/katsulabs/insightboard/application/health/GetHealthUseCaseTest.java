package com.katsulabs.insightboard.application.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.katsulabs.insightboard.domain.health.HealthStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GetHealthUseCase")
class GetHealthUseCaseTest {

    @DisplayName("헬스 조회 시 UP 상태를 반환한다")
    @Test
    void returnsUpStatus() {
        var snapshot = new GetHealthUseCase().execute();

        assertThat(snapshot.status()).isEqualTo(HealthStatus.UP);
        assertThat(snapshot.message()).isEqualTo("insight-board");
    }
}
