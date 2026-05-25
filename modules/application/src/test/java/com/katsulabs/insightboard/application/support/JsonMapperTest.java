package com.katsulabs.insightboard.application.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JsonMapper")
class JsonMapperTest {

    record Sample(String name, int count) {
    }

    @DisplayName("객체를 JSON 문자열로 직렬화·역직렬화한다")
    @Test
    void roundTrip() {
        String json = JsonMapper.toJson(new Sample("demo", 3));
        Sample restored = JsonMapper.fromJson(json, Sample.class);
        assertThat(restored.name()).isEqualTo("demo");
        assertThat(restored.count()).isEqualTo(3);
    }
}
