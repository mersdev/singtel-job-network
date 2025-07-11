package com.singtel.network.controller;

import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.dto.user.UserProfileResponse;
import com.singtel.network.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthController.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private JwtAuthenticationResponse authResponse;
    private UserProfileResponse userProfile;

    @BeforeEach
    void setUp() {
        // Create login request
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        // Create user profile
        userProfile = new UserProfileResponse();
        userProfile.setId(UUID.randomUUID());
        userProfile.setUsername("testuser");
        userProfile.setEmail("test@example.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");

        // Create auth response
        authResponse = new JwtAuthenticationResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setExpiresIn(3600000L);
        authResponse.setUser(userProfile);
    }

    @Test
    void login_Success() {
        // Arrange
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("access-token", response.getBody().getAccessToken());
        assertEquals("refresh-token", response.getBody().getRefreshToken());
        assertEquals("Bearer", response.getBody().getTokenType());
        assertEquals(3600000L, response.getBody().getExpiresIn());
        assertEquals("testuser", response.getBody().getUser().getUsername());
        assertEquals("test@example.com", response.getBody().getUser().getEmail());
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username/email or password"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.login(loginRequest);
        });
    }

    @Test
    void refreshToken_Success() {
        // Arrange
        Map<String, String> refreshRequest = Map.of("refreshToken", "valid-refresh-token");
        when(authService.refreshToken(anyString())).thenReturn(authResponse);

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = authController.refreshToken(refreshRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("access-token", response.getBody().getAccessToken());
        assertEquals("refresh-token", response.getBody().getRefreshToken());
        assertEquals("Bearer", response.getBody().getTokenType());
    }

    @Test
    void refreshToken_InvalidToken() {
        // Arrange
        Map<String, String> refreshRequest = Map.of("refreshToken", "invalid-token");
        when(authService.refreshToken(anyString()))
                .thenThrow(new BadCredentialsException("Invalid refresh token"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.refreshToken(refreshRequest);
        });
    }

    @Test
    void refreshToken_MissingToken() {
        // Arrange
        Map<String, String> refreshRequest = Map.of(); // Empty request

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = authController.refreshToken(refreshRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getCurrentUser_Success() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(userProfile);

        // Act
        ResponseEntity<UserProfileResponse> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("Test", response.getBody().getFirstName());
        assertEquals("User", response.getBody().getLastName());
    }

    @Test
    void logout_Success() {
        // Act
        ResponseEntity<Map<String, String>> response = authController.logout();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Logout successful", response.getBody().get("message"));
    }

    @Test
    void health_Success() {
        // Act
        ResponseEntity<Map<String, String>> response = authController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("Authentication Service", response.getBody().get("service"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }
}
