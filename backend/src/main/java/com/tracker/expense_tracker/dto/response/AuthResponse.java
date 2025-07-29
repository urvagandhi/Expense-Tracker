package com.tracker.expense_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long userId;
    private String email;
    private String username;
    private String role;

    public static AuthResponse of(String token, Long userId, String email, String username, String role) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(userId)
                .email(email)
                .username(username)
                .role(role)
                .build();
    }
}
