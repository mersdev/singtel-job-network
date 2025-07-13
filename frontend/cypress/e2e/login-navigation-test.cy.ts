describe('Login Navigation Test', () => {
  it('should navigate to dashboard after successful login', () => {
    // Clear any existing authentication
    cy.window().then((win) => {
      win.localStorage.clear();
    });

    cy.visit('/auth/login');
    
    // Fill login form with valid credentials
    cy.get('[data-cy="email-input"]').clear().type('jane.smith@techstart.sg');
    cy.get('[data-cy="password-input"]').clear().type('password123');
    
    // Intercept the login API call with correct endpoint pattern
    cy.intercept('POST', '**/api/auth/login').as('loginRequest');
    
    cy.get('[data-cy="login-button"]').click();
    
    // Wait for the API call to complete successfully
    cy.wait('@loginRequest').then((interception) => {
      expect(interception.response?.statusCode).to.equal(200);
    });
    
    // Check if tokens are stored
    cy.window().then((win) => {
      const token = win.localStorage.getItem('singtel_auth_token');
      const user = win.localStorage.getItem('singtel_user');
      expect(token).to.exist;
      expect(user).to.exist;
    });
    
    // Wait for navigation to complete
    cy.url({ timeout: 10000 }).should('include', '/dashboard');
    
    // Verify we're on the dashboard page
    cy.get('body').should('contain', 'Dashboard');
  });
});
