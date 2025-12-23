package com.carlos.todoapi.integration;

import com.carlos.todoapi.config.TestSecurityConfig;
import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.request.LoginRequest;
import com.carlos.todoapi.dto.request.RegisterRequest;
import com.carlos.todoapi.dto.response.LoginResponse;
import com.carlos.todoapi.entity.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "taskuser",
                "taskuser@test.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest("taskuser", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoginResponse.class
        );

        jwtToken = loginResponse.token();
    }

    @Test
    @DisplayName("Should create task successfully with JWT token")
    void testCreateTask_Success() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Integration Test Task",
                "Short description",
                TaskStatus.PENDING,
                null,
                null
        );

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.username").value("taskuser"));
    }

    @Test
    @DisplayName("Should return 401 when creating task without token")
    void testCreateTask_Unauthorized() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Unauthorized Task",
                null, null, null, null
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should list user tasks with pagination")
    void testGetTasks_Success() throws Exception {
        //Creating tasks
        for (int i = 0; i < 3; i++) {
            CreateTaskRequest request = new CreateTaskRequest(
                    "Task " + i, null, null, null, null
            );

            mockMvc.perform(post("/api/tasks")
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

        }
            mockMvc.perform(get("/api/tasks")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(3))
                    .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @DisplayName("Should filter tasks by status")
    void testGetTasks_FilterByStatus() throws Exception {
        CreateTaskRequest pending = new CreateTaskRequest(
                "Pending Task", null, TaskStatus.PENDING, null, null
        );

        CreateTaskRequest inProgress = new CreateTaskRequest(
                "In Progress Task", null, TaskStatus.IN_PROGRESS, null, null
        );

        CreateTaskRequest completed = new CreateTaskRequest(
                "Completed Task", null, TaskStatus.COMPLETED, null, null
        );

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pending)));

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inProgress)));

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completed)));

        //change to other status
        mockMvc.perform(get("/api/tasks?status=PENDING")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask_Success() throws Exception {

        CreateTaskRequest request = new CreateTaskRequest(
                "Task to Delete", null, null, null, null
        );

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long taskId = objectMapper.readTree(response).get("id").asLong();

        //delete
        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should return 403 when trying to access another user's task")
    void testGetTask_UnauthorizedAccess() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                "Private task", null, null, null, null
        );

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long taskId = objectMapper.readTree(response).get("id").asLong();

        RegisterRequest user2 = new RegisterRequest(
                "user2",
                "user2@test.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        LoginRequest login2 = new LoginRequest("user2", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login2)))
                .andReturn();

        LoginResponse loginResponse2 = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        mockMvc.perform(get("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + loginResponse2.token()))
                .andExpect(status().isForbidden());
    }
}
