/// <reference types="cypress" />

// Custom commands for authentication and common operations
declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Custom command to login with email and password
       * @example cy.login('user@example.com', 'password123')
       */
      login(email: string, password: string): Chainable<void>;
      
      /**
       * Custom command to login as test user
       * @example cy.loginAsTestUser()
       */
      loginAsTestUser(): Chainable<void>;
      
      /**
       * Custom command to login as admin user
       * @example cy.loginAsAdmin()
       */
      loginAsAdmin(): Chainable<void>;
      
      /**
       * Custom command to logout
       * @example cy.logout()
       */
      logout(): Chainable<void>;
      
      /**
       * Custom command to wait for API response
       * @example cy.waitForApi('@getServices')
       */
      waitForApi(alias: string): Chainable<void>;
      
      /**
       * Custom command to check if user is authenticated
       * @example cy.checkAuthenticated()
       */
      checkAuthenticated(): Chainable<void>;
    }
  }
}

// Login command
Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/auth/login');

  // Wait for the page to load and any overlays to disappear
  cy.get('[data-cy="email-input"]').should('be.visible');

  // Clear and type with force to handle any overlay issues
  cy.get('[data-cy="email-input"]').clear({ force: true }).type(email, { force: true });
  cy.get('[data-cy="password-input"]').clear({ force: true }).type(password, { force: true });
  cy.get('[data-cy="login-button"]').click({ force: true });

  // Wait for successful login and redirect
  cy.url().should('not.include', '/auth/login');
  cy.window().its('localStorage').invoke('getItem', 'singtel_auth_token').should('exist');
});

// Login as test user
Cypress.Commands.add('loginAsTestUser', () => {
  const testUser = Cypress.env('testUser');
  cy.login(testUser.email, testUser.password);
});

// Login as admin user
Cypress.Commands.add('loginAsAdmin', () => {
  const adminUser = Cypress.env('adminUser');
  cy.login(adminUser.email, adminUser.password);
});

// Logout command
Cypress.Commands.add('logout', () => {
  cy.get('[data-cy="user-menu"]').click();
  cy.get('[data-cy="logout-button"]').click();
  cy.url().should('include', '/auth/login');
  cy.window().its('localStorage').invoke('getItem', 'singtel_auth_token').should('not.exist');
});

// Wait for API response
Cypress.Commands.add('waitForApi', (alias: string) => {
  cy.wait(alias).then((interception) => {
    expect(interception.response?.statusCode).to.be.oneOf([200, 201, 204]);
  });
});

// Check if user is authenticated
Cypress.Commands.add('checkAuthenticated', () => {
  cy.window().its('localStorage').invoke('getItem', 'singtel_auth_token').should('exist');
  cy.get('[data-cy="user-menu"]').should('be.visible');
});
