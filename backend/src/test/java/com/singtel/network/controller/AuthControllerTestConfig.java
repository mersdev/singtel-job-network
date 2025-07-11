package com.singtel.network.controller;

import com.singtel.network.config.CorsProperties;
import com.singtel.network.exception.GlobalExceptionHandler;
import com.singtel.network.security.JwtAuthenticationEntryPoint;
import com.singtel.network.security.JwtAuthenticationFilter;
import com.singtel.network.security.JwtTokenProvider;
import com.singtel.network.service.AuthService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test configuration for AuthController tests.
 */
@TestConfiguration
@EnableWebSecurity
@Import(GlobalExceptionHandler.class)
public class AuthControllerTestConfig {

    @MockBean
    private AuthService authService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private CorsProperties corsProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
