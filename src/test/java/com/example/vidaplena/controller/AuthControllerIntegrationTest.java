package com.example.vidaplena.controller;

import com.example.vidaplena.domain.dto.request.LoginRequest;
import com.example.vidaplena.domain.dto.response.LoginResponse;
import com.example.vidaplena.domain.dto.response.UserResponse;
import com.example.vidaplena.domain.enums.UserRole;
import com.example.vidaplena.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthService authService;

        @Test
        @DisplayName("POST /api/auth/login deve retornar 200 e token com credenciais válidas")
        void shouldReturn200AndTokenWithValidCredentials() throws Exception {
                // Arrange
                LoginRequest loginRequest = LoginRequest.builder()
                                .email("admin@vidaplena.com")
                                .password("admin123")
                                .build();

                UserResponse userResponse = UserResponse.builder()
                                .id(UUID.randomUUID())
                                .name("Admin User")
                                .email("admin@vidaplena.com")
                                .role(UserRole.ADMIN)
                                .active(true)
                                .build();

                LoginResponse loginResponse = LoginResponse.builder()
                                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token")
                                .type("Bearer")
                                .expiresIn(86400000L)
                                .user(userResponse)
                                .build();

                when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

                // Act & Assert
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value(loginResponse.getToken()))
                                .andExpect(jsonPath("$.type").value("Bearer"))
                                .andExpect(jsonPath("$.user.email").value("admin@vidaplena.com"));
        }

        @Test
        @DisplayName("POST /api/auth/login deve retornar 400 com email inválido")
        void shouldReturn400WithInvalidEmail() throws Exception {
                // Arrange
                LoginRequest loginRequest = LoginRequest.builder()
                                .email("invalid-email")
                                .password("password123")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/auth/login deve retornar 400 com senha vazia")
        void shouldReturn400WithEmptyPassword() throws Exception {
                // Arrange
                LoginRequest loginRequest = LoginRequest.builder()
                                .email("admin@vidaplena.com")
                                .password("")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest());
        }
}
