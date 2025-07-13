describe('Debug Authentication', () => {
  it('should debug login process step by step', () => {
    // Clear any existing authentication
    cy.window().then((win) => {
      win.localStorage.clear();
    });

    cy.visit('/auth/login');

    // Fill login form with valid credentials
    cy.get('[data-cy="email-input"]').clear().type('jane.smith@techstart.sg');
    cy.get('[data-cy="password-input"]').clear().type('password123');

    // Intercept the login API call with detailed logging
    cy.intercept('POST', '**/auth/login', (req) => {
      console.log('=== LOGIN REQUEST DETAILS ===');
      console.log('URL:', req.url);
      console.log('Method:', req.method);
      console.log('Headers:', req.headers);
      console.log('Body:', req.body);
      console.log('============================');
    }).as('loginRequest');

    cy.get('[data-cy="login-button"]').click();

    // Wait for the API call to complete
    cy.wait('@loginRequest', { timeout: 10000 }).then((interception) => {
      console.log('=== LOGIN RESPONSE DETAILS ===');
      console.log('Status:', interception.response?.statusCode);
      console.log('Headers:', interception.response?.headers);
      console.log('Body:', interception.response?.body);
      console.log('==============================');

      if (interception.response?.statusCode === 200) {
        cy.log('Login API successful');

        // Check if tokens are stored after successful API call
        cy.window().then((win) => {
          const token = win.localStorage.getItem('singtel_auth_token');
          const user = win.localStorage.getItem('singtel_user');
          cy.log('Token in localStorage: ' + (token ? 'EXISTS' : 'NULL'));
          cy.log('User in localStorage: ' + (user ? 'EXISTS' : 'NULL'));
        });

        // Wait for any async operations and navigation
        cy.wait(3000);

        // Check current URL after login
        cy.url().then((url) => {
          cy.log('Current URL after login: ' + url);
        });
      } else {
        cy.log('Login API failed with status: ' + interception.response?.statusCode);
        cy.log('Error response: ' + JSON.stringify(interception.response?.body));
      }
    });
  });
});
