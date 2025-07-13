describe('Profile Management', () => {
  beforeEach(() => {
    // Login before each test
    cy.loginAsTestUser();

    // Set up common intercepts
    cy.intercept('GET', '**/auth/me', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'jane.smith',
        email: 'jane.smith@techstart.sg',
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '+65 9234 5678',
        role: 'USER',
        status: 'ACTIVE',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      }
    }).as('getProfile');
  });

  it('should display user profile information', () => {
    cy.visit('/profile');

    // Check that profile page loads
    cy.get('[data-cy="profile-header"]').should('contain', 'User Profile');

    // Wait for profile to load
    cy.waitForApi('@getProfile');

    // Check that profile form is visible
    cy.get('[data-cy="profile-form"]').should('be.visible');
    cy.get('[data-cy="first-name-input"]').should('have.value', 'Jane');
    cy.get('[data-cy="last-name-input"]').should('have.value', 'Smith');
    cy.get('[data-cy="email-input"]').should('have.value', 'jane.smith@techstart.sg');

    // Check readonly fields
    cy.get('[data-cy="username-display"]').should('contain', 'jane.smith');
    cy.get('[data-cy="role-display"]').should('contain', 'USER');
    cy.get('[data-cy="status-display"]').should('contain', 'ACTIVE');
  });

  it('should update profile information successfully', () => {
    cy.fixture('users').then((users) => {
      // Set up successful update intercept with delay to see loading state
      cy.intercept('PUT', '**/auth/profile', (req) => {
        req.reply({
          delay: 1000, // Add delay to see loading state
          statusCode: 200,
          body: {
            success: true,
            data: {
              id: 1,
              username: 'jane.smith',
              email: users.updatedProfile.email,
              firstName: users.updatedProfile.firstName,
              lastName: users.updatedProfile.lastName,
              phone: users.updatedProfile.phone,
              role: 'USER',
              status: 'ACTIVE',
              createdAt: '2024-01-01T00:00:00Z',
              updatedAt: new Date().toISOString()
            }
          }
        });
      }).as('updateProfile');

      cy.visit('/profile');

      // Wait for profile to load
      cy.waitForApi('@getProfile');

      // Update profile information
      cy.get('[data-cy="first-name-input"]').clear().type(users.updatedProfile.firstName);
      cy.get('[data-cy="last-name-input"]').clear().type(users.updatedProfile.lastName);
      cy.get('[data-cy="email-input"]').clear().type(users.updatedProfile.email);
      cy.get('[data-cy="phone-input"]').clear().type(users.updatedProfile.phone);

      // Submit the form
      cy.get('[data-cy="save-button"]').click();

      // Check loading state (should appear quickly due to delay)
      cy.get('[data-cy="save-button"]').should('contain', 'Saving...');
      cy.get('[data-cy="save-button"]').should('be.disabled');

      // Wait for update to complete
      cy.waitForApi('@updateProfile');

      // Check success message
      cy.get('[data-cy="success-message"]').should('be.visible');
      cy.get('[data-cy="success-message"]').should('contain', 'Profile updated successfully!');

      // Verify form shows updated values
      cy.get('[data-cy="first-name-input"]').should('have.value', users.updatedProfile.firstName);
      cy.get('[data-cy="last-name-input"]').should('have.value', users.updatedProfile.lastName);
      cy.get('[data-cy="email-input"]').should('have.value', users.updatedProfile.email);
      cy.get('[data-cy="phone-input"]').should('have.value', users.updatedProfile.phone);
    });
  });

  it('should validate required fields', () => {
    cy.visit('/profile');

    // Wait for profile to load
    cy.waitForApi('@getProfile');

    // Clear required fields and trigger validation
    cy.get('[data-cy="first-name-input"]').clear().blur();
    cy.get('[data-cy="last-name-input"]').clear().blur();
    cy.get('[data-cy="email-input"]').clear().blur();

    // Should show validation errors
    cy.get('[data-cy="first-name-error"]').should('be.visible');
    cy.get('[data-cy="first-name-error"]').should('contain', 'First name is required');
    cy.get('[data-cy="last-name-error"]').should('be.visible');
    cy.get('[data-cy="last-name-error"]').should('contain', 'Last name is required');
    cy.get('[data-cy="email-error"]').should('be.visible');
    cy.get('[data-cy="email-error"]').should('contain', 'Email is required');

    // Save button should be disabled
    cy.get('[data-cy="save-button"]').should('be.disabled');
  });

  it('should validate email format', () => {
    cy.visit('/profile');

    // Wait for profile to load
    cy.waitForApi('@getProfile');

    // Enter invalid email
    cy.get('[data-cy="email-input"]').clear().type('invalid-email').blur();

    // Should show email validation error
    cy.get('[data-cy="email-error"]').should('be.visible');
    cy.get('[data-cy="email-error"]').should('contain', 'Please enter a valid email address');
  });

  it('should reload profile data', () => {
    cy.visit('/profile');

    // Wait for profile to load
    cy.waitForApi('@getProfile');

    // Set up reload intercept
    cy.intercept('GET', '**/auth/me', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'jane.smith',
        email: 'jane.smith@techstart.sg',
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '+65 9234 5678',
        role: 'USER',
        status: 'ACTIVE',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: new Date().toISOString()
      }
    }).as('reloadProfile');

    // Click reload button
    cy.get('[data-cy="reload-profile"]').click();

    // Wait for reload to complete
    cy.waitForApi('@reloadProfile');

    // Verify profile data is still displayed
    cy.get('[data-cy="first-name-input"]').should('have.value', 'Jane');
    cy.get('[data-cy="last-name-input"]').should('have.value', 'Smith');
    cy.get('[data-cy="email-input"]').should('have.value', 'jane.smith@techstart.sg');
  });

  it('should handle profile update errors', () => {
    // Mock server error
    cy.intercept('PUT', '**/auth/profile', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('updateProfileError');

    cy.visit('/profile');

    // Wait for profile to load
    cy.waitForApi('@getProfile');

    // Try to update profile
    cy.get('[data-cy="first-name-input"]').clear().type('Updated');
    cy.get('[data-cy="save-button"]').click();

    // Wait for error response
    cy.wait('@updateProfileError');

    // Should show error message (check for any error message)
    cy.get('[data-cy="error-message"]').should('be.visible');
    cy.get('[data-cy="error-message"]').should('contain.text', 'error');
  });

  it('should handle authentication errors during profile operations', () => {
    // Mock 401 error for profile update
    cy.intercept('PUT', '**/auth/profile', {
      statusCode: 401,
      body: { message: 'Unauthorized' }
    }).as('updateProfileUnauthorized');

    cy.visit('/profile');

    // Wait for initial profile load
    cy.waitForApi('@getProfile');

    // Try to update profile
    cy.get('[data-cy="first-name-input"]').clear().type('Updated');
    cy.get('[data-cy="save-button"]').click();

    // Wait for unauthorized response
    cy.wait('@updateProfileUnauthorized');

    // Should show error message (check for any error message)
    cy.get('[data-cy="error-message"]').should('be.visible');
    cy.get('[data-cy="error-message"]').should('contain.text', 'Unauthorized');
  });
});
