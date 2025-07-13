package com.singtel.network.repository;

import com.singtel.network.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Find user by username or email with company relationship loaded
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmailWithCompany(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Find user by ID with company relationship loaded
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.company WHERE u.id = :id")
    Optional<User> findByIdWithCompany(@Param("id") UUID id);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by company ID
     */
    List<User> findByCompanyId(UUID companyId);

    /**
     * Find users by company ID with pagination
     */
    Page<User> findByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Find users by status
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * Find users by role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find users by company ID and role
     */
    List<User> findByCompanyIdAndRole(UUID companyId, User.UserRole role);

    /**
     * Find users by company ID and status
     */
    List<User> findByCompanyIdAndStatus(UUID companyId, User.UserStatus status);

    /**
     * Find active users by company ID
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.status = 'ACTIVE'")
    List<User> findActiveUsersByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Find admin users by company ID
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.role = 'ADMIN' AND u.status = 'ACTIVE'")
    List<User> findAdminUsersByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Update last login time
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginTime WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") UUID userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Update user status
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") UUID userId, @Param("status") User.UserStatus status);

    /**
     * Find users with expired passwords
     */
    @Query("SELECT u FROM User u WHERE u.passwordChangedAt < :expiryDate AND u.status = 'ACTIVE'")
    List<User> findUsersWithExpiredPasswords(@Param("expiryDate") LocalDateTime expiryDate);

    /**
     * Count users by company ID
     */
    long countByCompanyId(UUID companyId);

    /**
     * Count active users by company ID
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.company.id = :companyId AND u.status = 'ACTIVE'")
    long countActiveUsersByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Search users by name or email
     */
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND u.company.id = :companyId")
    Page<User> searchUsersByCompany(@Param("companyId") UUID companyId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);
}
