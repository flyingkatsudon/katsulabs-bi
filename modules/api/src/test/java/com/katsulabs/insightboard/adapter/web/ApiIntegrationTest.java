package com.katsulabs.insightboard.adapter.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.katsulabs.insightboard.application.support.JsonMapper;
import com.katsulabs.insightboard.security.SessionHeaderRegistry;
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
                .andExpect(jsonPath("$.rows.length()").value(20));

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

        String body = JsonMapper.toJson(new BoardUpdatePayload("Demo Board Updated", 1L, "{}"));
        mockMvc.perform(put("/api/v1/boards/2")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
    }

    @DisplayName("로그인 후 카테고리 CRUD")
    @Test
    void loginThenCategoryCrud() throws Exception {
        MockHttpSession session = loginAsAdmin();
        String unique = "Cat_" + System.nanoTime();
        String createBody = JsonMapper.toJson(new CategoryPayload(unique));

        MvcResult created = mockMvc.perform(post("/api/v1/categories")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        long id = JsonMapper.mapper()
                .readTree(created.getResponse().getContentAsString())
                .get("id")
                .asLong();

        mockMvc.perform(get("/api/v1/categories").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + unique + "')]").exists());

        String updateBody = JsonMapper.toJson(new CategoryPayload(unique + "_renamed"));
        mockMvc.perform(put("/api/v1/categories/" + id)
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));

        mockMvc.perform(delete("/api/v1/categories/" + id).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
    }

    @DisplayName("로그인 후 부동산·경제 지표 데모 데이터셋을 조회한다")
    @Test
    void loginThenDemoRealEstateAndEconomic() throws Exception {
        MockHttpSession session = loginAsAdmin();

        mockMvc.perform(get("/api/v1/datasources").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'demo_realestate')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'demo_economic')]").exists());

        mockMvc.perform(get("/api/v1/datasets").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'realestate_korea')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'economic_indicators')]").exists());
    }

    @DisplayName("Chart Gallery 위젯 집계 새로고침(reload)이 동작한다")
    @Test
    void loginThenChartGalleryWidgetReload() throws Exception {
        MockHttpSession session = loginAsAdmin();

        MvcResult widgets = mockMvc.perform(get("/api/v1/widgets").session(session))
                .andExpect(status().isOk())
                .andReturn();

        long widgetId = -1L;
        var arr = JsonMapper.mapper().readTree(widgets.getResponse().getContentAsString());
        for (var node : arr) {
            if ("demo_line".equals(node.path("name").asText())) {
                widgetId = node.path("id").asLong();
                break;
            }
        }

        org.junit.jupiter.api.Assumptions.assumeTrue(widgetId > 0, "demo_line widget not seeded");

        MvcResult detail = mockMvc.perform(get("/api/v1/widgets/" + widgetId).session(session))
                .andExpect(status().isOk())
                .andReturn();

        var dataJson = JsonMapper.mapper()
                .readTree(detail.getResponse().getContentAsString())
                .path("dataJson")
                .asText();
        var root = JsonMapper.mapper().readTree(dataJson);
        long datasetId = root.path("datasetId").asLong();
        long datasourceId = root.path("datasource").asLong();

        String aggCfg =
                "{\"rows\":[{\"columnName\":\"the_year\"}],\"columns\":[],\"filters\":[],\"values\":[{\"column\":\"store_sales\",\"aggType\":\"sum\"}]}";
        String aggBody =
                """
                {"datasetId":%d,"datasourceId":%d,"query":{},"cfg":%s,"reload":%s}
                """
                        .formatted(
                                datasetId,
                                datasourceId,
                                JsonMapper.toJson(aggCfg),
                                "true");

        mockMvc.perform(post("/api/v1/aggregate")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(aggBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("세션 없을 때 session API 는 204 를 반환한다")
    @Test
    void sessionWithoutCookieReturnsNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/auth/session")).andExpect(status().isNoContent());
    }

    @DisplayName("로그인 후 X-Insightboard-Session 헤더만으로 boards 조회")
    @Test
    void loginThenBoardsWithSessionHeader() throws Exception {
        MockHttpSession session = loginAsAdmin();
        String sessionId = session.getId();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/boards")
                        .header(SessionHeaderRegistry.SESSION_HEADER, sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Demo Board' || @.name == 'Demo Board Updated')]").exists());
    }

    @DisplayName("로그인 후 세션 API 로 사용자 정보를 조회한다")
    @Test
    void loginThenSessionEndpoint() throws Exception {
        MockHttpSession session = loginAsAdmin();
        mockMvc.perform(get("/api/v1/auth/session").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("admin01"))
                .andExpect(jsonPath("$.roleId").value("1"));
    }

    @DisplayName("로그인 후 대시보드 목록을 조회한다")
    @Test
    void loginThenListBoards() throws Exception {
        MockHttpSession session = loginAsAdmin();
        mockMvc.perform(get("/api/v1/boards").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Demo Board' || @.name == 'Demo Board Updated')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Chart Gallery')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'FoodMart Sample Dashboard')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Real Estate Sample Dashboard')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Economic Indicators Sample Dashboard')]").exists());
    }

    @DisplayName("Viewer 는 게시되지 않은 보드 상세 조회가 404")
    @Test
    void viewerCannotGetUnpublishedBoard() throws Exception {
        MockHttpSession session = login("viewer01", "admin123");
        mockMvc.perform(get("/api/v1/boards/2").session(session)).andExpect(status().isNotFound());
    }

    @DisplayName("Viewer 는 게시된 FoodMart 보드를 조회한다")
    @Test
    void viewerCanGetPublishedBoard() throws Exception {
        MockHttpSession session = login("viewer01", "admin123");
        mockMvc.perform(get("/api/v1/boards/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publishedToViewers").value(true));
    }

    @DisplayName("Viewer 세션에 defaultBoardId 가 포함된다")
    @Test
    void viewerSessionHasDefaultBoard() throws Exception {
        MockHttpSession session = login("viewer01", "admin123");
        mockMvc.perform(get("/api/v1/auth/session").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultBoardId").value(1));
    }

    @DisplayName("Viewer 는 보드 생성이 거부된다")
    @Test
    void viewerCannotCreateBoard() throws Exception {
        MockHttpSession session = login("viewer01", "admin123");
        String body = JsonMapper.toJson(new BoardUpdatePayload("New Board", 1L, "{}"));
        mockMvc.perform(post("/api/v1/boards")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @DisplayName("Manager 는 보드 생성이 허용된다")
    @Test
    void managerCanCreateBoard() throws Exception {
        MockHttpSession session = login("manager01", "admin123");
        String body = JsonMapper.toJson(new BoardUpdatePayload("Mgr Board " + System.nanoTime(), 1L, "{}"));
        mockMvc.perform(post("/api/v1/boards")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
    }

    @DisplayName("Super Admin 은 사용자 목록을 조회한다")
    @Test
    void adminListsUsers() throws Exception {
        MockHttpSession session = loginAsAdmin();
        mockMvc.perform(get("/api/v1/users").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userId == 'viewer01')]").exists())
                .andExpect(jsonPath("$[?(@.userId == 'manager01')]").exists());
    }

    private MockHttpSession loginAsAdmin() throws Exception {
        return login("admin01", "admin123");
    }

    private MockHttpSession login(String userId, String password) throws Exception {
        String body = JsonMapper.toJson(new LoginPayload(userId, password));
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andReturn();
        return (MockHttpSession) login.getRequest().getSession();
    }

    private record LoginPayload(String userId, String password) {
    }

    private record BoardUpdatePayload(String name, Long categoryId, String layoutJson) {
    }

    private record DatasourcePayload(String name, String type, String configJson) {
    }

    private record CategoryPayload(String name) {
    }
}
