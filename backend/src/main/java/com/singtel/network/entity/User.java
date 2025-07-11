package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity representing individual users associated with companies.
 * Implements UserDetails for Spring Security integration.
 */
@Entity
@Table(name = "users", schema = "singtel_app")
public class User extends BaseEntity implements UserDetails {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password hash must not exceed 255 characters")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    // Constructors
    public User() {
    }

    public User(String username, String email, String passwordHash, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordChangedAt = LocalDateTime.now();
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.INACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Consider credentials expired if password is older than 90 days
        if (passwordChangedAt == null) {
            return false;
        }
        return passwordChangedAt.isAfter(LocalDateTime.now().minusDays(90));
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.passwordChangedAt = LocalDateTime.now();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean canManageServices() {
        return role == UserRole.ADMIN || role == UserRole.USER;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }

    /**
     * User role enumeration
     */
    public enum UserRole {
        ADMIN, USER, VIEWER
    }

    /**
     * User status enumeration
     */
    public enum UserStatus {
        ACTIVE, SUSPENDED, INACTIVE
    }
}
