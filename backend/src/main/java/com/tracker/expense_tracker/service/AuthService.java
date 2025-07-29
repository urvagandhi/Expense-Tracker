package com.tracker.expense_tracker.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tracker.expense_tracker.dto.request.LoginRequest;
import com.tracker.expense_tracker.dto.request.RegisterRequest;
import com.tracker.expense_tracker.dto.response.AuthResponse;
import com.tracker.expense_tracker.entity.Role;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.exception.DuplicateResourceException;
import com.tracker.expense_tracker.repository.UserRepository;
import com.tracker.expense_tracker.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);
        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getDisplayName(), user.getRole().name());
    }
}
