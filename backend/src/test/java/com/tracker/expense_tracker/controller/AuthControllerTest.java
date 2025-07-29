package com.tracker.expense_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.expense_tracker.config.SecurityConfig;
import com.tracker.expense_tracker.dto.request.LoginRequest;
import com.tracker.expense_tracker.dto.request.RegisterRequest;
import com.tracker.expense_tracker.dto.response.AuthResponse;
import com.tracker.expense_tracker.security.JwtAuthenticationFilter;
import com.tracker.expense_tracker.security.JwtService;
import com.tracker.expense_tracker.service.AuthService;
import com.tracker.expense_tracker.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(value = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}))
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserService userService;

    @Test
    @DisplayName("POST /api/auth/register - should register user")
    @WithMockUser
    void register_Success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("John").email("john@example.com").password("password123").build();

        AuthResponse authResponse = AuthResponse.of("jwt-token", 1L, "john@example.com", "John", "USER");
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 400 for invalid input")
    @WithMockUser
    void register_ValidationError() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("").email("not-an-email").password("12").build();

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - should login user")
    @WithMockUser
    void login_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com").password("password123").build();

        AuthResponse authResponse = AuthResponse.of("jwt-token", 1L, "john@example.com", "John", "USER");
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }
}
