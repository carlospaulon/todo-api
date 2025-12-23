package com.carlos.todoapi.integration;

import com.carlos.todoapi.config.TestSecurityConfig;
import com.carlos.todoapi.dto.request.LoginRequest;
import com.carlos.todoapi.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register user successfully via API")
    void testRegisterUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "integration_user",
                "integration@test.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integration_user"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Should return 409 when registering duplicate username")
    void testRegisterUser_DuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "duplicate_user",
                "user1@test.com",
                "password123"
        );

        //first register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        //second register (duplicate)
        RegisterRequest duplicateRequest = new RegisterRequest(
                "duplicate_user",
                "user2@test.com",
                "password456"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should return 400 when registration data is invalid")
    void testRegisterUser_InvalidData() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest(
                "ab",
                "invalid-email",
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());

    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void testLogin_Success() throws Exception {
        //register first
        RegisterRequest registerRequest = new RegisterRequest(
                "login_user",
                "login@test.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        //login
        LoginRequest loginRequest = new LoginRequest("login_user", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("login_user"))
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    @DisplayName("Should return 401 when login with wrong password")
    void testLogin_WrongPassword() throws Exception {
        //register first
        RegisterRequest registerRequest = new RegisterRequest(
                "login_user",
                "login@test.com",
                "correct_password"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        //login
        LoginRequest loginRequest = new LoginRequest("login_user", "wrong_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

    }
}
