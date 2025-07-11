package com.singtel.network.service;

import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.dto.user.UserProfileResponse;
import com.singtel.network.entity.Company;
import com.singtel.network.entity.User;
import com.singtel.network.repository.UserRepository;
import com.singtel.network.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Company testCompany;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Create test company
        testCompany = new Company();
        testCompany.setId(UUID.randomUUID());
        testCompany.setName("Test Company");
        testCompany.setRegistrationNumber("TEST123");
        testCompany.setEmail("test@company.com");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.UserRole.USER);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setCompany(testCompany);
        testUser.setPasswordChangedAt(LocalDateTime.now());

        // Create login request
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(testUser.getUsername())).thenReturn("refresh-token");
        when(tokenProvider.getRemainingValidityTime("access-token")).thenReturn(3600000L);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresIn());
        assertNotNull(response.getUser());
        assertEquals(testUser.getUsername(), response.getUser().getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(tokenProvider).generateRefreshToken(testUser.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Invalid username/email or password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenProvider);
        verifyNoInteractions(userRepository);
    }

    @Test
    void refreshToken_Success() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(testUser.getUsername());
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateTokenFromUsername(testUser.getUsername())).thenReturn("new-access-token");
        when(tokenProvider.generateRefreshToken(testUser.getUsername())).thenReturn("new-refresh-token");
        when(tokenProvider.getRemainingValidityTime("new-access-token")).thenReturn(3600000L);

        // Act
        JwtAuthenticationResponse response = authService.refreshToken(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresIn());
        assertNotNull(response.getUser());
        assertEquals(testUser.getUsername(), response.getUser().getUsername());

        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).isRefreshToken(refreshToken);
        verify(tokenProvider).getUsernameFromToken(refreshToken);
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void refreshToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid-token";
        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.refreshToken(invalidToken);
        });

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(tokenProvider).validateToken(invalidToken);
        verifyNoMoreInteractions(tokenProvider);
        verifyNoInteractions(userRepository);
    }

    @Test
    void refreshToken_NotRefreshToken() {
        // Arrange
        String accessToken = "access-token";
        when(tokenProvider.validateToken(accessToken)).thenReturn(true);
        when(tokenProvider.isRefreshToken(accessToken)).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.refreshToken(accessToken);
        });

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(tokenProvider).validateToken(accessToken);
        verify(tokenProvider).isRefreshToken(accessToken);
        verifyNoInteractions(userRepository);
    }

    @Test
    void refreshToken_UserNotFound() {
        // Arrange
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.refreshToken(refreshToken);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void refreshToken_DisabledUser() {
        // Arrange
        testUser.setStatus(User.UserStatus.INACTIVE);
        String refreshToken = "valid-refresh-token";
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(refreshToken)).thenReturn(testUser.getUsername());
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.refreshToken(refreshToken);
        });

        assertEquals("User account is disabled", exception.getMessage());
        verify(userRepository).findByUsername(testUser.getUsername());
    }

    @Test
    void getCurrentUser_Success() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser);

        // Act
        UserProfileResponse response = authService.getCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getFirstName(), response.getFirstName());
        assertEquals(testUser.getLastName(), response.getLastName());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getStatus(), response.getStatus());
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.getCurrentUser();
        });

        assertEquals("No authenticated user found", exception.getMessage());
    }

    @Test
    void logout_Success() {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        // Act
        authService.logout();

        // Assert
        verify(securityContext).getAuthentication();
        // SecurityContextHolder.clearContext() is called internally
    }
}
