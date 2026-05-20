package com.insightboard.app.api.cboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.insightboard.app.presentation.auth.LoginRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CboardDataPlaneTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void login() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "admin"))))
                .andExpect(status().isOk())
                .andReturn();
        token = objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    void getBoardData_hydratesWidgetsInLayout() throws Exception {
        MvcResult list = mockMvc.perform(get("/cboard/dashboard/getBoardList")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode boards = objectMapper.readTree(list.getResponse().getContentAsString());
        assertThat(boards.size()).isGreaterThan(0);
        long boardId = boards.get(0).get("id").asLong();

        MvcResult board = mockMvc.perform(get("/cboard/dashboard/getBoardData?id=" + boardId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode layout = objectMapper.readTree(board.getResponse().getContentAsString()).get("layout");
        JsonNode widgets = layout.get("rows").get(0).get("widgets");
        assertThat(widgets.get(0).get("widget").get("data")).isNotNull();
    }

    @Test
    void getAggregateData_withAggConfig_groupsByKwdA() throws Exception {
        String cfg =
                "{\"rows\":[{\"columnName\":\"kwd_a\"}],\"values\":[{\"column\":\"doc_cnt_both\",\"aggType\":\"sum\"}]}";
        MvcResult result = mockMvc.perform(post("/cboard/dashboard/getAggregateData?datasetId=1&cfg="
                                + java.net.URLEncoder.encode(cfg, java.nio.charset.StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("columnList").size()).isEqualTo(2);
        assertThat(body.get("data").size()).isGreaterThan(0);
    }

    @Test
    void viewAggDataQuery_returnsSql() throws Exception {
        String cfg = "{\"rows\":[{\"columnName\":\"doc_date\"}],\"values\":[{\"column\":\"doc_cnt_both\",\"aggType\":\"sum\"}]}";
        MvcResult result = mockMvc.perform(post("/cboard/dashboard/viewAggDataQuery?datasetId=1&cfg="
                                + java.net.URLEncoder.encode(cfg, java.nio.charset.StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode arr = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(arr.get(0).asText()).contains("GROUP BY doc_date");
    }

    @Test
    void getAggregateData_postAndGet() throws Exception {
        mockMvc.perform(post("/cboard/dashboard/getAggregateData?datasetId=1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/cboard/dashboard/getAggregateData?datasetId=1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("columnList")).isNotEmpty();
        assertThat(body.get("data")).isNotEmpty();
    }

    @Test
    void getColumns_returnsSchema() throws Exception {
        MvcResult result = mockMvc.perform(post("/cboard/dashboard/getColumns?datasetId=1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("columns").size()).isGreaterThan(0);
    }
}
