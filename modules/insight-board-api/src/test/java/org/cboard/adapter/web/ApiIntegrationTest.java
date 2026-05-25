package org.cboard.adapter.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cboard.application.support.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("local-h2")
@DisplayName("API 통합")
class ApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @DisplayName("로그인 후 JDBC 데이터소스를 등록하고 연결 테스트한다")
    @Test
    void loginThenCreateDatasource() throws Exception {
        MockHttpSession session = loginAsAdmin();
        String config =
                """
                {"driver":"org.h2.Driver","jdbcurl":"jdbc:h2:mem:insight-board;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE","username":"sa","password":""}
                """;
        String uniqueName = "test_source_" + System.nanoTime();
        String body = JsonMapper.toJson(new DatasourcePayload(uniqueName, "jdbc", config));

        mockMvc.perform(post("/api/v1/datasources/test")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));

        mockMvc.perform(post("/api/v1/datasources")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"))
                .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("로그인 후 데모 데이터셋 미리보기와 데이터소스 테스트")
    @Test
    void loginThenPreviewDemoDataset() throws Exception {
        MockHttpSession session = loginAsAdmin();

        mockMvc.perform(post("/api/v1/datasources/1/test").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));

        mockMvc.perform(get("/api/v1/datasets/1/preview").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columns").isArray())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows.length()").value(5));

        String previewBody =
                """
                {"datasourceId":1,"sql":"SELECT * FROM sales_fact_sample_flat","limit":5}
                """;
        mockMvc.perform(post("/api/v1/datasets/preview-query")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(previewBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columns").isArray())
                .andExpect(jsonPath("$.rows.length()").value(5));

        String aggCfg =
                "{\"rows\":[{\"columnName\":\"the_year\"}],\"columns\":[],\"filters\":[],\"values\":[{\"column\":\"store_sales\",\"aggType\":\"sum\"}]}";
        String aggBody =
                """
                {"datasetId":1,"datasourceId":1,"query":{},"cfg":%s,"reload":false}
                """
                        .formatted(JsonMapper.toJson(aggCfg));
        mockMvc.perform(post("/api/v1/aggregate")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(aggBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columnList").isArray())
                .andExpect(jsonPath("$.data").isArray());

        mockMvc.perform(post("/api/v1/aggregate/view-query")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(aggBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sql").isNotEmpty());
    }

    @DisplayName("로그인 후 대시보드 이름을 수정한다")
    @Test
    void loginThenUpdateBoard() throws Exception {
        MockHttpSession session = loginAsAdmin();

        String body = JsonMapper.toJson(new BoardUpdatePayload("Demo Board", 1L, "{}"));
        mockMvc.perform(put("/api/v1/boards/1")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
    }

    @DisplayName("로그인 후 대시보드 목록을 조회한다")
    @Test
    void loginThenListBoards() throws Exception {
        MockHttpSession session = loginAsAdmin();
        mockMvc.perform(get("/api/v1/boards").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Demo Board"));
    }

    private MockHttpSession loginAsAdmin() throws Exception {
        String body = JsonMapper.toJson(new LoginPayload("admin01", "admin123"));
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("admin01"))
                .andReturn();
        return (MockHttpSession) login.getRequest().getSession();
    }

    private record LoginPayload(String userId, String password) {
    }

    private record BoardUpdatePayload(String name, Long categoryId, String layoutJson) {
    }

    private record DatasourcePayload(String name, String type, String configJson) {
    }
}
