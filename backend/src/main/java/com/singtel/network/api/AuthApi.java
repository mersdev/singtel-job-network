package com.singtel.network.api;

import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.dto.user.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API interface for authentication operations.
 */
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public interface AuthApi {

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(
                        schema = @Schema(implementation = JwtAuthenticationResponse.class),
                        examples = @ExampleObject(
                            name = "Successful Login Response",
                            value = """
                                {
                                  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNjk4ODMwNDAwfQ.example",
                                  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNzAxMzM2MDAwfQ.example",
                                  "tokenType": "Bearer",
                                  "expiresIn": 86400000,
                                  "user": {
                                    "id": "880e8400-e29b-41d4-a716-446655440001",
                                    "username": "john.doe",
                                    "email": "john.doe@techstart.sg",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "phone": "+65 9123 4567",
                                    "role": "ADMIN",
                                    "status": "ACTIVE",
                                    "company": {
                                      "id": "770e8400-e29b-41d4-a716-446655440001",
                                      "name": "TechStart Pte Ltd",
                                      "registrationNumber": "202301234A"
                                    }
                                  }
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Invalid Credentials",
                            value = """
                                {
                                  "status": 401,
                                  "error": "Authentication failed",
                                  "message": "Invalid username/email or password",
                                  "path": "uri=/api/auth/login",
                                  "timestamp": "2024-07-12T03:27:35.207436"
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Validation Error",
                            value = """
                                {
                                  "status": 400,
                                  "error": "Validation failed",
                                  "message": "Invalid input data",
                                  "path": "uri=/api/auth/login",
                                  "timestamp": "2024-07-12T03:27:35.207436",
                                  "validationErrors": {
                                    "usernameOrEmail": "Username or email is required",
                                    "password": "Password must be between 6 and 100 characters"
                                  }
                                }
                                """
                        )
                    ))
    })
    ResponseEntity<JwtAuthenticationResponse> login(
        @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = LoginRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Login with Username",
                        value = """
                            {
                              "usernameOrEmail": "john.doe",
                              "password": "password123",
                              "rememberMe": false
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Login with Email",
                        value = """
                            {
                              "usernameOrEmail": "john.doe@techstart.sg",
                              "password": "password123",
                              "rememberMe": true
                            }
                            """
                    )
                }
            )
        ) LoginRequest loginRequest);

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(
                        schema = @Schema(implementation = JwtAuthenticationResponse.class),
                        examples = @ExampleObject(
                            name = "Token Refresh Response",
                            value = """
                                {
                                  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNjk4ODMwNDAwfQ.newtoken",
                                  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNzAxMzM2MDAwfQ.newrefresh",
                                  "tokenType": "Bearer",
                                  "expiresIn": 86400000,
                                  "user": {
                                    "id": "880e8400-e29b-41d4-a716-446655440001",
                                    "username": "john.doe",
                                    "email": "john.doe@techstart.sg",
                                    "firstName": "John",
                                    "lastName": "Doe"
                                  }
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Invalid Refresh Token",
                            value = """
                                {
                                  "status": 401,
                                  "error": "Authentication failed",
                                  "message": "Invalid refresh token",
                                  "path": "uri=/api/auth/refresh",
                                  "timestamp": "2024-07-12T03:27:35.207436"
                                }
                                """
                        )
                    ))
    })
    ResponseEntity<JwtAuthenticationResponse> refreshToken(
        @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = @ExampleObject(
                    name = "Refresh Token Request",
                    value = """
                        {
                          "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNzAxMzM2MDAwfQ.example"
                        }
                        """
                )
            )
        ) Map<String, String> request);

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user profile", description = "Get profile information of the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
                    content = @Content(
                        schema = @Schema(implementation = UserProfileResponse.class),
                        examples = @ExampleObject(
                            name = "User Profile Response",
                            value = """
                                {
                                  "id": "880e8400-e29b-41d4-a716-446655440001",
                                  "username": "john.doe",
                                  "email": "john.doe@techstart.sg",
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "phone": "+65 9123 4567",
                                  "role": "ADMIN",
                                  "status": "ACTIVE",
                                  "lastLoginAt": "2024-07-12T03:27:35.207436",
                                  "company": {
                                    "id": "770e8400-e29b-41d4-a716-446655440001",
                                    "name": "TechStart Pte Ltd",
                                    "registrationNumber": "202301234A",
                                    "email": "admin@techstart.sg",
                                    "phone": "+65 6123 4567",
                                    "address": "1 Marina Bay Sands, Level 10",
                                    "postalCode": "018956",
                                    "country": "Singapore",
                                    "industry": "Technology",
                                    "companySize": "SMALL",
                                    "status": "ACTIVE"
                                  }
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Unauthorized Access",
                            value = """
                                {
                                  "path": "/auth/me",
                                  "error": "Unauthorized",
                                  "message": "Access denied. Please provide valid authentication credentials.",
                                  "timestamp": "2024-07-12T03:27:35.207436"
                                }
                                """
                        )
                    ))
    })
    ResponseEntity<UserProfileResponse> getCurrentUser();

    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User logout", description = "Logout user and invalidate session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Logout Success",
                            value = """
                                {
                                  "message": "Logout successful"
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Unauthorized Logout",
                            value = """
                                {
                                  "path": "/auth/logout",
                                  "error": "Unauthorized",
                                  "message": "Access denied. Please provide valid authentication credentials.",
                                  "timestamp": "2024-07-12T03:27:35.207436"
                                }
                                """
                        )
                    ))
    })
    ResponseEntity<Map<String, String>> logout();

    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication service health check", description = "Check if authentication service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy",
                content = @Content(
                    examples = @ExampleObject(
                        name = "Health Check Response",
                        value = """
                            {
                              "timestamp": "2024-07-12T03:27:35.207436",
                              "status": "UP",
                              "service": "Authentication Service"
                            }
                            """
                    )
                ))
    ResponseEntity<Map<String, String>> health();
}
