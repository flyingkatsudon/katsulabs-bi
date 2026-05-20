package com.insightboard.app.api.cboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.insightboard.app.presentation.auth.LoginRequest;
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
class CboardCommonsTest {

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
    void getMenuList_includesConfigForAdmin() throws Exception {
        mockMvc.perform(get("/cboard/commons/getMenuList").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.menuCode == 'config')].menuCode").value("config"));
    }

    @Test
    void changePwd_success() throws Exception {
        mockMvc.perform(get("/cboard/commons/changePwd")
                        .param("curPwd", "admin")
                        .param("newPwd", "admin2")
                        .param("cfmPwd", "admin2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("1"));
        mockMvc.perform(get("/cboard/commons/changePwd")
                        .param("curPwd", "admin2")
                        .param("newPwd", "admin")
                        .param("cfmPwd", "admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.status").value("1"));
    }

    @Test
    void persist_storesSnapshot() throws Exception {
        String body = "{\"persistId\":\"test-1\",\"data\":{\"k\":\"v\"}}";
        mockMvc.perform(post("/cboard/commons/persist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("test-1"));
    }
}
