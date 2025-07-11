package com.singtel.network.controller;

import com.singtel.network.api.AuthApi;
import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.dto.user.UserProfileResponse;
import com.singtel.network.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());
        
        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);
        
        logger.info("Login successful for user: {}", loginRequest.getUsernameOrEmail());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        logger.info("Token refresh attempt");
        
        JwtAuthenticationResponse response = authService.refreshToken(refreshToken);
        
        logger.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse userProfile = authService.getCurrentUser();
        return ResponseEntity.ok(userProfile);
    }

    @Override
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        
        logger.info("User logout successful");
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @Override
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Authentication Service",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
