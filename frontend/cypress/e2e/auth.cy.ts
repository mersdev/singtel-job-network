describe('Authentication Flow', () => {
  beforeEach(() => {
    // Clear any existing authentication
    cy.window().then((win) => {
      win.localStorage.clear();
    });
  });

  it('should redirect unauthenticated users to login page', () => {
    cy.visit('/dashboard');
    cy.url().should('include', '/auth/login');
    cy.get('[data-cy="login-form"]').should('be.visible');
  });

  it('should redirect unauthenticated users from profile page to login', () => {
    cy.visit('/profile');
    cy.url().should('include', '/auth/login');
    cy.url().should('include', 'returnUrl=%2Fprofile');
  });

  it('should login successfully with valid credentials', () => {
    cy.visit('/auth/login');

    // Use real test user credentials from backend test script
    cy.get('[data-cy="email-input"]').clear().type('john.doe');
    cy.get('[data-cy="password-input"]').clear().type('password123');
    cy.get('[data-cy="login-button"]').click();

    // Should redirect to dashboard after successful login using real API endpoint /api/auth/login
    cy.url({ timeout: 15000 }).should('include', '/dashboard');
    cy.get('[data-cy="user-menu"]').should('be.visible');

    // Check that token is stored
    cy.window().its('localStorage').invoke('getItem', 'singtel_auth_token').should('exist');
  });

  it('should show error message for invalid credentials', () => {
    cy.visit('/auth/login');

    // Fill login form with invalid credentials using real API endpoint /api/auth/login
    cy.get('[data-cy="email-input"]').type('invalid@email.com');
    cy.get('[data-cy="password-input"]').type('wrongpassword');
    cy.get('[data-cy="login-button"]').click();

    // Should show error message from real API response
    cy.get('[data-cy="error-message"]').should('be.visible');
    cy.get('[data-cy="error-message"]').should('contain', 'Invalid username/email or password');

    // Should remain on login page
    cy.url().should('include', '/auth/login');
  });

  it('should logout successfully', () => {
    cy.loginAsTestUser();
    
    // Verify user is logged in
    cy.url().should('include', '/dashboard');
    cy.checkAuthenticated();
    
    // Logout
    cy.logout();
    
    // Should redirect to login page
    cy.url().should('include', '/auth/login');
    cy.window().its('localStorage').invoke('getItem', 'singtel_auth_token').should('not.exist');
  });

  it('should handle session expiration gracefully', () => {
    cy.loginAsTestUser();
    
    // Simulate expired token by clearing it
    cy.window().then((win) => {
      win.localStorage.removeItem('singtel_auth_token');
    });
    
    // Try to access protected route
    cy.visit('/profile');
    
    // Should redirect to login
    cy.url().should('include', '/auth/login');
  });
});
