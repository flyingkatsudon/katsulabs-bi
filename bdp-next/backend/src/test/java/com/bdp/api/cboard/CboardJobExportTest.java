package com.bdp.api.cboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bdp.api.auth.LoginRequest;
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
class CboardJobExportTest {

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
    void jobCrudAndExec() throws Exception {
        String json = "{\"name\":\"Daily mail\",\"cronExp\":\"0 0 8 * * *\",\"jobType\":\"mail\"}";
        mockMvc.perform(get("/cboard/dashboard/saveJob").param("json", json).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));

        mockMvc.perform(get("/cboard/dashboard/getJobList").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Daily mail"));

        MvcResult list = mockMvc.perform(get("/cboard/dashboard/getJobList")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        long jobId = objectMapper.readTree(list.getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc.perform(get("/cboard/dashboard/execJob?id=" + jobId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
    }

    @Test
    void exportBoard_returnsAttachment() throws Exception {
        MvcResult boards = mockMvc.perform(get("/cboard/dashboard/getBoardList")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        long boardId = objectMapper.readTree(boards.getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc.perform(get("/cboard/dashboard/exportBoard?id=" + boardId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("report.xls")));
    }
}
