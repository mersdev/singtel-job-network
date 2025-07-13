package com.singtel.network.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for Singtel Network On-Demand API
 * Configures JWT authentication and comprehensive API documentation
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Singtel Business Network On-Demand API",
        version = "1.0.0",
        description = """
            RESTful API for managing network services on-demand.
            
            This API provides comprehensive functionality for:
            - User authentication and authorization
            - Service catalog management
            - Order placement and tracking
            - Bandwidth management
            - Service monitoring and metrics
            - Real-time service provisioning
            
            ## Authentication
            This API uses JWT (JSON Web Token) for authentication. To access protected endpoints:
            1. Login using the `/auth/login` endpoint to obtain an access token
            2. Include the token in the Authorization header: `Bearer <your-token>`
            3. Tokens expire after 24 hours - use the refresh token to obtain a new access token
            
            ## Rate Limiting
            API calls are rate-limited to prevent abuse:
            - 100 requests per minute for authenticated users
            - 20 requests per minute for unauthenticated endpoints
            
            ## Error Handling
            The API uses standard HTTP status codes and returns detailed error messages in JSON format.
            """,
        contact = @Contact(
            name = "Singtel Development Team",
            email = "dev@singtel.com",
            url = "https://www.singtel.com"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://www.singtel.com/terms"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8088/api",
            description = "Development Server"
        ),
        @Server(
            url = "https://api-staging.singtel-network.com/api",
            description = "Staging Server"
        ),
        @Server(
            url = "https://api.singtel-network.com/api",
            description = "Production Server"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = """
        JWT Authentication
        
        To authenticate:
        1. Login via POST /auth/login with valid credentials
        2. Copy the 'accessToken' from the response
        3. Click 'Authorize' button above and enter: Bearer <your-access-token>
        4. All subsequent API calls will include the authentication header
        
        Example token format: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY5ODc0NDAwMCwiZXhwIjoxNjk4ODMwNDAwfQ.example
        """
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addResponses("UnauthorizedError", createUnauthorizedResponse())
                .addResponses("ForbiddenError", createForbiddenResponse())
                .addResponses("NotFoundError", createNotFoundResponse())
                .addResponses("ValidationError", createValidationErrorResponse())
                .addResponses("InternalServerError", createInternalServerErrorResponse())
                .addExamples("LoginRequest", createLoginRequestExample())
                .addExamples("UnauthorizedResponse", createUnauthorizedExample())
                .addExamples("ValidationErrorResponse", createValidationErrorExample())
            );
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
            .description("Authentication required - Invalid or missing JWT token")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .example("""
                        {
                          "timestamp": "2024-01-15T10:30:00Z",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "JWT token is invalid or expired",
                          "path": "/api/services"
                        }
                        """)
                )
            );
    }

    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
            .description("Access forbidden - Insufficient permissions")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .example("""
                        {
                          "timestamp": "2024-01-15T10:30:00Z",
                          "status": 403,
                          "error": "Forbidden",
                          "message": "Access denied - insufficient permissions",
                          "path": "/api/admin/users"
                        }
                        """)
                )
            );
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
            .description("Resource not found")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .example("""
                        {
                          "timestamp": "2024-01-15T10:30:00Z",
                          "status": 404,
                          "error": "Not Found",
                          "message": "Service with ID 'invalid-id' not found",
                          "path": "/api/services/invalid-id"
                        }
                        """)
                )
            );
    }

    private ApiResponse createValidationErrorResponse() {
        return new ApiResponse()
            .description("Validation error - Invalid request data")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .example("""
                        {
                          "timestamp": "2024-01-15T10:30:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Validation failed",
                          "errors": [
                            {
                              "field": "requestedBandwidthMbps",
                              "message": "Bandwidth must be between 10 and 10000 Mbps"
                            },
                            {
                              "field": "contactEmail",
                              "message": "Please provide a valid email address"
                            }
                          ],
                          "path": "/api/orders"
                        }
                        """)
                )
            );
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
            .description("Internal server error")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .example("""
                        {
                          "timestamp": "2024-01-15T10:30:00Z",
                          "status": 500,
                          "error": "Internal Server Error",
                          "message": "An unexpected error occurred",
                          "path": "/api/services"
                        }
                        """)
                )
            );
    }

    private Example createLoginRequestExample() {
        Example example = new Example();
        example.setSummary("Login Request");
        example.setDescription("Example login request with valid credentials");
        example.setValue("""
            {
              "username": "john.doe",
              "password": "SecurePassword123!"
            }
            """);
        return example;
    }

    private Example createUnauthorizedExample() {
        Example example = new Example();
        example.setSummary("Unauthorized Access");
        example.setDescription("Response when JWT token is missing or invalid");
        example.setValue("""
            {
              "timestamp": "2024-01-15T10:30:00Z",
              "status": 401,
              "error": "Unauthorized",
              "message": "JWT token is invalid or expired",
              "path": "/api/services"
            }
            """);
        return example;
    }

    private Example createValidationErrorExample() {
        Example example = new Example();
        example.setSummary("Validation Error");
        example.setDescription("Response when request validation fails");
        example.setValue("""
            {
              "timestamp": "2024-01-15T10:30:00Z",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "errors": [
                {
                  "field": "requestedBandwidthMbps",
                  "message": "Bandwidth must be between 10 and 10000 Mbps"
                }
              ],
              "path": "/api/orders"
            }
            """);
        return example;
    }
}
