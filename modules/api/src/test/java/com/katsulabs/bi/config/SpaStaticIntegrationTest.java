package com.katsulabs.bi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles({"local-h2", "prod"})
@DisplayName("SPA 정적 서빙 (prod)")
class SpaStaticIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /index.html 과 SPA 라우트는 인증 없이 HTML을 반환한다")
    void servesIndexWithoutAuth() throws Exception {
        var index =
                mockMvc.perform(get("/index.html")).andExpect(status().isOk()).andReturn();
        assertThat(index.getResponse().getContentAsString()).contains("katsulabs-bi-spa-test");

        var boards = mockMvc.perform(get("/boards")).andExpect(status().isOk()).andReturn();
        assertThat(boards.getResponse().getContentAsString()).contains("katsulabs-bi-spa-test");
    }

    @Test
    @DisplayName("API는 여전히 인증이 필요하다")
    void apiStillRequiresAuth() throws Exception {
        var api = mockMvc.perform(get("/api/v1/boards")).andReturn();
        assertThat(api.getResponse().getStatus()).isIn(401, 403);
    }
}
