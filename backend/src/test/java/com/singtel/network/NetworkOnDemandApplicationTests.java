package com.singtel.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.singtel.network.dto.auth.JwtAuthenticationResponse;
import com.singtel.network.dto.auth.LoginRequest;
import com.singtel.network.entity.Company;
import com.singtel.network.entity.User;
import com.singtel.network.repository.CompanyRepository;
import com.singtel.network.repository.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
@Transactional
class NetworkOnDemandApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Company testCompany;
    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        userRepository.deleteAll();
        companyRepository.deleteAll();

        // Create test company
        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setRegistrationNumber("TEST123");
        testCompany.setEmail("test@company.com");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);
        testCompany = companyRepository.save(testCompany);

        // Create test user
        testUser = new User();
        testUser.setCompany(testCompany);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.UserRole.ADMIN);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setPasswordChangedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);
    }

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertNotNull(mockMvc);
        assertNotNull(userRepository);
        assertNotNull(companyRepository);
    }

    @Test
    void authenticationFlow_Success() throws Exception {
        // Test complete authentication flow
        
        // 1. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andReturn();

        // Extract access token
        String responseContent = loginResult.getResponse().getContentAsString();
        JwtAuthenticationResponse authResponse = objectMapper.readValue(responseContent, JwtAuthenticationResponse.class);
        accessToken = authResponse.getAccessToken();
        assertNotNull(accessToken);

        // 2. Access protected endpoint
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));

        // 3. Access service catalog (protected endpoint)
        mockMvc.perform(get("/services/categories")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // 4. Logout
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void unauthorizedAccess_Denied() throws Exception {
        // Test that protected endpoints require authentication
        
        // Try to access protected endpoint without token
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());

        // Try to access service catalog without token
        mockMvc.perform(get("/services/categories"))
                .andExpect(status().isUnauthorized());

        // Try to access orders without token
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidCredentials_Rejected() throws Exception {
        // Test login with invalid credentials
        
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setUsernameOrEmail("testuser");
        invalidLogin.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Authentication failed"));
    }

    @Test
    void validationErrors_Handled() throws Exception {
        // Test validation error handling
        
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsernameOrEmail(""); // Invalid empty username
        invalidRequest.setPassword("123"); // Invalid short password

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void healthEndpoints_Accessible() throws Exception {
        // Test that health endpoints are accessible without authentication
        
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Authentication Service"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void serviceCatalogEndpoints_RequireAuthentication() throws Exception {
        // First authenticate
        authenticateTestUser();

        // Test service catalog endpoints
        mockMvc.perform(get("/services/categories")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/services")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/services/types")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void orderEndpoints_RequireAuthentication() throws Exception {
        // First authenticate
        authenticateTestUser();

        // Test order endpoints
        mockMvc.perform(get("/orders")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/orders/statistics")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private void authenticateTestUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        JwtAuthenticationResponse authResponse = objectMapper.readValue(responseContent, JwtAuthenticationResponse.class);
        accessToken = authResponse.getAccessToken();
    }
}
