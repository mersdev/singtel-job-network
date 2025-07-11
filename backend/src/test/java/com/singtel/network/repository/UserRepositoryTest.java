package com.singtel.network.repository;

import com.singtel.network.entity.Company;
import com.singtel.network.entity.User;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 */
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.default_schema=singtel_app",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:test-schema.sql"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Create test company
        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setRegistrationNumber("TEST123");
        testCompany.setEmail("test@company.com");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);
        testCompany = entityManager.persistAndFlush(testCompany);

        // Create test users
        testUser1 = new User();
        testUser1.setCompany(testCompany);
        testUser1.setUsername("testuser1");
        testUser1.setEmail("test1@example.com");
        testUser1.setPasswordHash("$2a$10$hashedpassword1");
        testUser1.setFirstName("Test");
        testUser1.setLastName("User1");
        testUser1.setRole(User.UserRole.ADMIN);
        testUser1.setStatus(User.UserStatus.ACTIVE);
        testUser1.setPasswordChangedAt(LocalDateTime.now());
        testUser1 = entityManager.persistAndFlush(testUser1);

        testUser2 = new User();
        testUser2.setCompany(testCompany);
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPasswordHash("$2a$10$hashedpassword2");
        testUser2.setFirstName("Test");
        testUser2.setLastName("User2");
        testUser2.setRole(User.UserRole.USER);
        testUser2.setStatus(User.UserStatus.ACTIVE);
        testUser2.setPasswordChangedAt(LocalDateTime.now());
        testUser2 = entityManager.persistAndFlush(testUser2);

        entityManager.clear();
    }

    @Test
    void findByUsername_Success() {
        // Act
        Optional<User> result = userRepository.findByUsername("testuser1");

        // Assert
        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals("testuser1", user.getUsername());
        assertEquals("test1@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User1", user.getLastName());
        assertEquals(User.UserRole.ADMIN, user.getRole());
    }

    @Test
    void findByUsername_NotFound() {
        // Act
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_Success() {
        // Act
        Optional<User> result = userRepository.findByEmail("test1@example.com");

        // Assert
        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals("testuser1", user.getUsername());
        assertEquals("test1@example.com", user.getEmail());
    }

    @Test
    void findByUsernameOrEmail_WithUsername() {
        // Act
        Optional<User> result = userRepository.findByUsernameOrEmail("testuser1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser1", result.get().getUsername());
    }

    @Test
    void findByUsernameOrEmail_WithEmail() {
        // Act
        Optional<User> result = userRepository.findByUsernameOrEmail("test1@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test1@example.com", result.get().getEmail());
    }

    @Test
    void existsByUsername_True() {
        // Act
        boolean exists = userRepository.existsByUsername("testuser1");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_False() {
        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByEmail_True() {
        // Act
        boolean exists = userRepository.existsByEmail("test1@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_False() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByCompanyId_Success() {
        // Act
        List<User> users = userRepository.findByCompanyId(testCompany.getId());

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser2")));
    }

    @Test
    void findByCompanyIdWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<User> userPage = userRepository.findByCompanyId(testCompany.getId(), pageable);

        // Assert
        assertNotNull(userPage);
        assertEquals(2, userPage.getTotalElements());
        assertEquals(1, userPage.getContent().size());
        assertEquals(2, userPage.getTotalPages());
    }

    @Test
    void findByStatus_Success() {
        // Act
        List<User> activeUsers = userRepository.findByStatus(User.UserStatus.ACTIVE);

        // Assert
        assertNotNull(activeUsers);
        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(u -> u.getStatus() == User.UserStatus.ACTIVE));
    }

    @Test
    void findByRole_Success() {
        // Act
        List<User> adminUsers = userRepository.findByRole(User.UserRole.ADMIN);

        // Assert
        assertNotNull(adminUsers);
        assertEquals(1, adminUsers.size());
        assertEquals("testuser1", adminUsers.get(0).getUsername());
        assertEquals(User.UserRole.ADMIN, adminUsers.get(0).getRole());
    }

    @Test
    void findByCompanyIdAndRole_Success() {
        // Act
        List<User> adminUsers = userRepository.findByCompanyIdAndRole(testCompany.getId(), User.UserRole.ADMIN);

        // Assert
        assertNotNull(adminUsers);
        assertEquals(1, adminUsers.size());
        assertEquals("testuser1", adminUsers.get(0).getUsername());
        assertEquals(User.UserRole.ADMIN, adminUsers.get(0).getRole());
    }

    @Test
    void findActiveUsersByCompanyId_Success() {
        // Act
        List<User> activeUsers = userRepository.findActiveUsersByCompanyId(testCompany.getId());

        // Assert
        assertNotNull(activeUsers);
        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(u -> u.getStatus() == User.UserStatus.ACTIVE));
    }

    @Test
    void findAdminUsersByCompanyId_Success() {
        // Act
        List<User> adminUsers = userRepository.findAdminUsersByCompanyId(testCompany.getId());

        // Assert
        assertNotNull(adminUsers);
        assertEquals(1, adminUsers.size());
        assertEquals("testuser1", adminUsers.get(0).getUsername());
        assertEquals(User.UserRole.ADMIN, adminUsers.get(0).getRole());
        assertEquals(User.UserStatus.ACTIVE, adminUsers.get(0).getStatus());
    }

    @Test
    void countByCompanyId_Success() {
        // Act
        long count = userRepository.countByCompanyId(testCompany.getId());

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countActiveUsersByCompanyId_Success() {
        // Act
        long count = userRepository.countActiveUsersByCompanyId(testCompany.getId());

        // Assert
        assertEquals(2, count);
    }

    @Test
    void searchUsersByCompany_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> userPage = userRepository.searchUsersByCompany(testCompany.getId(), "Test", pageable);

        // Assert
        assertNotNull(userPage);
        assertEquals(2, userPage.getTotalElements());
        assertTrue(userPage.getContent().stream().allMatch(u -> 
            u.getFirstName().toLowerCase().contains("test") || 
            u.getLastName().toLowerCase().contains("test")));
    }

    @Test
    void searchUsersByCompany_WithEmail() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> userPage = userRepository.searchUsersByCompany(testCompany.getId(), "test1@example.com", pageable);

        // Assert
        assertNotNull(userPage);
        assertEquals(1, userPage.getTotalElements());
        assertEquals("test1@example.com", userPage.getContent().get(0).getEmail());
    }

    @Test
    void updateLastLoginTime_Success() {
        // Arrange
        LocalDateTime loginTime = LocalDateTime.now();

        // Act
        userRepository.updateLastLoginTime(testUser1.getId(), loginTime);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<User> updatedUser = userRepository.findById(testUser1.getId());
        assertTrue(updatedUser.isPresent());
        assertNotNull(updatedUser.get().getLastLoginAt());
        // Note: Due to precision differences, we check if the time is close
        assertTrue(Math.abs(loginTime.toEpochSecond(java.time.ZoneOffset.UTC) - 
                           updatedUser.get().getLastLoginAt().toEpochSecond(java.time.ZoneOffset.UTC)) < 2);
    }

    @Test
    void updateUserStatus_Success() {
        // Act
        userRepository.updateUserStatus(testUser1.getId(), User.UserStatus.SUSPENDED);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<User> updatedUser = userRepository.findById(testUser1.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(User.UserStatus.SUSPENDED, updatedUser.get().getStatus());
    }

    @Test
    void findUsersWithExpiredPasswords_Success() {
        // Arrange
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1); // Future date

        // Act
        List<User> expiredUsers = userRepository.findUsersWithExpiredPasswords(expiryDate);

        // Assert
        assertNotNull(expiredUsers);
        assertEquals(2, expiredUsers.size()); // Both users have passwords changed today, so they're "expired" relative to future date
    }
}
