package com.singtel.network.service;

import com.singtel.network.entity.User;
import com.singtel.network.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserDetailsService implementation for Spring Security authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);

        User user = userRepository.findByUsernameOrEmailWithCompany(usernameOrEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });

        logger.debug("User found: {} with role: {} and status: {} and company: {}",
                    user.getUsername(), user.getRole(), user.getStatus(),
                    user.getCompany() != null ? user.getCompany().getName() : "null");

        return user;
    }

    /**
     * Load user by ID (useful for JWT token validation)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        try {
            User user = userRepository.findById(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

            logger.debug("User found by ID: {} with username: {}", userId, user.getUsername());
            return user;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID format: {}", userId);
            throw new UsernameNotFoundException("Invalid user ID format: " + userId);
        }
    }
}
