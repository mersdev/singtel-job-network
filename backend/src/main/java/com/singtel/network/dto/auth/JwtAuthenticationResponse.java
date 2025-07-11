package com.singtel.network.dto.auth;

import com.singtel.network.dto.user.UserProfileResponse;

/**
 * DTO for JWT authentication response.
 */
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserProfileResponse user;

    // Constructors
    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, String refreshToken, long expiresIn, UserProfileResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserProfileResponse getUser() {
        return user;
    }

    public void setUser(UserProfileResponse user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                '}';
    }
}
