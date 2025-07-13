package com.singtel.network.service;

import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.dto.user.UserProfileResponse;
import com.singtel.network.entity.User;
import com.singtel.network.repository.UserRepository;
import com.singtel.network.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling authentication operations.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticate user and generate JWT tokens
     */
    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsernameOrEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = (User) authentication.getPrincipal();
            
            // Update last login time
            user.updateLastLogin();
            userRepository.save(user);

            // Generate tokens
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

            // Get token expiration time
            long expiresIn = tokenProvider.getRemainingValidityTime(accessToken);

            // Create user profile response
            UserProfileResponse userProfile = new UserProfileResponse(user);

            logger.info("User authenticated successfully: {}", user.getUsername());

            return new JwtAuthenticationResponse(accessToken, refreshToken, expiresIn, userProfile);

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            throw new BadCredentialsException("Invalid username/email or password");
        }
    }

    /**
     * Refresh JWT token using refresh token
     */
    public JwtAuthenticationResponse refreshToken(String refreshToken) {
        logger.info("Attempting to refresh token");

        try {
            // Validate refresh token
            if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Invalid refresh token");
            }

            // Get username from refresh token
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            
            // Load user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Check if user is still active
            if (!user.isEnabled()) {
                throw new BadCredentialsException("User account is disabled");
            }

            // Generate new tokens
            String newAccessToken = tokenProvider.generateTokenFromUsername(username);
            String newRefreshToken = tokenProvider.generateRefreshToken(username);

            // Get token expiration time
            long expiresIn = tokenProvider.getRemainingValidityTime(newAccessToken);

            // Create user profile response
            UserProfileResponse userProfile = new UserProfileResponse(user);

            logger.info("Token refreshed successfully for user: {}", username);

            return new JwtAuthenticationResponse(newAccessToken, newRefreshToken, expiresIn, userProfile);

        } catch (BadCredentialsException e) {
            logger.error("Token refresh failed", e);
            throw e; // Re-throw BadCredentialsException with original message
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("No authenticated user found");
        }

        try {
            // Get the user from the authentication principal
            Object principal = authentication.getPrincipal();

            logger.debug("Authentication principal type: {}", principal.getClass().getName());

            if (!(principal instanceof User)) {
                logger.error("Authentication principal is not a User instance: {}", principal.getClass().getName());
                throw new BadCredentialsException("Invalid authentication principal");
            }

            User user = (User) principal;
            logger.debug("User from principal: {} (ID: {})", user.getUsername(), user.getId());

            // Reload user from database to ensure all relationships are loaded
            User fullUser = userRepository.findByIdWithCompany(user.getId())
                .orElseThrow(() -> {
                    logger.error("User not found in database with ID: {}", user.getId());
                    return new BadCredentialsException("User not found in database");
                });

            logger.debug("Retrieved current user: {} with company: {}",
                        fullUser.getUsername(),
                        fullUser.getCompany() != null ? fullUser.getCompany().getName() : "null");

            return new UserProfileResponse(fullUser);

        } catch (ClassCastException e) {
            logger.error("Failed to cast authentication principal to User", e);
            throw new BadCredentialsException("Invalid authentication state");
        } catch (Exception e) {
            logger.error("Error retrieving current user", e);
            throw new BadCredentialsException("Failed to retrieve user information");
        }
    }

    /**
     * Logout user (invalidate tokens on client side)
     */
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            logger.info("User logged out: {}", user.getUsername());
        }

        SecurityContextHolder.clearContext();
    }

    /**
     * Validate if user can access the system
     */
    private void validateUserAccess(User user) {
        if (!user.isEnabled()) {
            throw new BadCredentialsException("User account is disabled");
        }

        if (!user.isAccountNonLocked()) {
            throw new BadCredentialsException("User account is locked");
        }

        if (!user.isAccountNonExpired()) {
            throw new BadCredentialsException("User account has expired");
        }

        if (user.getCompany() == null || user.getCompany().getStatus() != com.singtel.network.entity.Company.CompanyStatus.ACTIVE) {
            throw new BadCredentialsException("Company account is not active");
        }
    }
}
