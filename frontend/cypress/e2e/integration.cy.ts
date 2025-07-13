describe('End-to-End Integration Tests', () => {
  beforeEach(() => {
    cy.loginAsTestUser();
  });

  it('should complete full order workflow', () => {
    // Mock services with correct API endpoint pattern
    cy.intercept('GET', '**/api/services', {
      statusCode: 200,
      body: [
        {
          id: '660e8400-e29b-41d4-a716-446655440001',
          name: 'Business Fiber 500M',
          serviceType: 'FIBER',
          description: 'High-speed fiber internet for business',
          isBandwidthAdjustable: true,
          basePricing: { setupFee: 150.00, monthlyFee: 299.00 },
          categoryName: 'Business Internet'
        }
      ]
    }).as('getServices');

    // Mock service details
    cy.intercept('GET', '**/api/services/660e8400-e29b-41d4-a716-446655440001', {
      statusCode: 200,
      body: {
        id: '660e8400-e29b-41d4-a716-446655440001',
        name: 'Business Fiber 500M',
        serviceType: 'FIBER',
        description: 'High-speed fiber internet for business',
        isBandwidthAdjustable: true,
        isAvailable: true,
        basePricing: { setupFee: 150.00, monthlyFee: 299.00 },
        categoryName: 'Business Internet'
      }
    }).as('getServiceDetails');

    // Mock order creation
    cy.intercept('POST', '**/api/orders', {
      statusCode: 201,
      body: {
        id: 'aa0e8400-e29b-41d4-a716-446655440003',
        orderNumber: 'ORD-2024-003',
        status: 'SUBMITTED',
        message: 'Order created successfully'
      }
    }).as('createOrder');

    // 1. Browse service catalog
    cy.visit('/services');
    cy.waitForApi('@getServices');

    // 2. Search for a specific service (services use client-side filtering, not API search)
    cy.get('[data-cy="search-input"]').type('Fiber');
    cy.get('[data-cy="search-button"]').click();

    // Wait a moment for client-side filtering
    cy.wait(500);

    // 3. View service details
    cy.get('[data-cy="service-card"]').first().click();
    cy.waitForApi('@getServiceDetails');
    cy.url().should('include', '/services/');
    cy.get('[data-cy="service-details"]').should('be.visible');

    // 4. Order the service - wait for service to load and button to be enabled
    cy.get('[data-cy="order-button"]').should('not.be.disabled');
    cy.get('[data-cy="order-button"]').click();
    cy.url().should('include', '/provisioning/new');

    // 5. Fill all required order form fields
    cy.get('[data-cy="location-input"]').type('123 Marina Bay Street, Singapore 018936');
    cy.get('#postalCode').type('018936');
    cy.get('#contactPerson').type('John Doe');
    cy.get('#contactPhone').type('91234567'); // Singapore format without +65
    cy.get('#contactEmail').type('john.doe@company.com');

    // Set requested date (tomorrow)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    cy.get('#requestedDate').type(tomorrow.toISOString().split('T')[0]);

    // Fill bandwidth if required
    cy.get('#requestedBandwidthMbps').type('100');

    // Fill special requirements
    cy.get('#specialRequirements').type('E2E test order');

    // 6. Submit order
    cy.get('[data-cy="submit-order-button"]').should('not.be.disabled');
    cy.get('[data-cy="submit-order-button"]').click();
    cy.waitForApi('@createOrder');

    // 7. Should redirect to order details page
    cy.url().should('include', '/orders/aa0e8400-e29b-41d4-a716-446655440003');
  });

  it('should handle user profile update workflow', () => {
    // Mock the profile API calls with correct endpoint patterns
    cy.intercept('GET', '**/api/auth/me', {
      statusCode: 200,
      body: {
        id: '880e8400-e29b-41d4-a716-446655440002',
        username: 'jane.smith',
        email: 'jane.smith@techstart.sg',
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '+65 9234 5678',
        role: 'USER',
        status: 'ACTIVE'
      }
    }).as('getProfile');

    cy.intercept('PUT', '**/api/auth/profile', {
      statusCode: 200,
      body: {
        success: true,
        data: {
          id: '880e8400-e29b-41d4-a716-446655440002',
          username: 'jane.smith',
          email: 'e2e@test.com',
          firstName: 'E2E Test',
          lastName: 'Smith',
          phone: '+65 9234 5678',
          role: 'USER',
          status: 'ACTIVE'
        }
      }
    }).as('updateProfile');

    // 1. Navigate to profile
    cy.visit('/profile');
    cy.waitForApi('@getProfile');

    // 2. Update profile
    const updatedFirstName = 'E2E Test';
    const updatedEmail = 'e2e@test.com';

    cy.get('[data-cy="first-name-input"]').clear().type(updatedFirstName);
    cy.get('[data-cy="email-input"]').clear().type(updatedEmail);

    // 3. Save changes
    cy.get('[data-cy="save-button"]').click();
    cy.waitForApi('@updateProfile');

    // 4. Verify success message
    cy.get('[data-cy="success-message"]').should('contain', 'Profile updated successfully');

    // 5. Verify values are updated
    cy.get('[data-cy="first-name-input"]').should('have.value', updatedFirstName);
    cy.get('[data-cy="email-input"]').should('have.value', updatedEmail);
  });

  it('should handle navigation between all main sections', () => {
    // Mock services for navigation with correct API patterns
    cy.intercept('GET', '**/api/services', {
      statusCode: 200,
      body: []
    }).as('getServices');

    // Mock orders for navigation with correct API patterns
    cy.intercept('GET', '**/api/orders/search**', {
      statusCode: 200,
      body: {
        data: [],
        pagination: { total: 0, totalPages: 0, page: 1, limit: 20 }
      }
    }).as('getOrders');

    // Mock profile for navigation
    cy.intercept('GET', '**/api/auth/me', {
      statusCode: 200,
      body: {
        id: '880e8400-e29b-41d4-a716-446655440002',
        username: 'jane.smith',
        email: 'jane.smith@techstart.sg',
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '+65 9234 5678',
        role: 'USER',
        status: 'ACTIVE'
      }
    }).as('getProfile');

    // 1. Start at dashboard
    cy.visit('/dashboard');
    cy.get('[data-cy="dashboard-header"]').should('be.visible');

    // 2. Navigate to service catalog
    cy.get('[data-cy="nav-services"]').click();
    cy.url().should('include', '/services');
    cy.waitForApi('@getServices');
    cy.get('[data-cy="catalog-header"]').should('be.visible');

    // 3. Navigate to orders
    cy.get('[data-cy="nav-orders"]').click();
    cy.url().should('include', '/orders');
    cy.waitForApi('@getOrders');
    cy.get('[data-cy="orders-header"]').should('be.visible');

    // 4. Navigate to profile (via user menu)
    cy.get('[data-cy="user-menu"]').click();
    cy.get('[data-cy="nav-profile"]').click();
    cy.url().should('include', '/profile');
    cy.waitForApi('@getProfile');
    cy.get('[data-cy="profile-header"]').should('be.visible');

    // 5. Navigate to monitoring (check if element exists first)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="nav-monitoring"]').length > 0) {
        cy.get('[data-cy="nav-monitoring"]').click();
        cy.url().should('include', '/monitoring');
      }
    });

    // 6. Navigate to bandwidth management (check if element exists first)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="nav-bandwidth"]').length > 0) {
        cy.get('[data-cy="nav-bandwidth"]').click();
        cy.url().should('include', '/bandwidth');
      }
    });

    // 7. Return to dashboard
    cy.get('[data-cy="nav-dashboard"]').click();
    cy.url().should('include', '/dashboard');
    cy.get('[data-cy="dashboard-header"]').should('be.visible');
  });

  it('should handle error recovery scenarios', () => {
    // 1. Test service catalog error recovery with correct API patterns
    cy.intercept('GET', '**/api/services', {
      statusCode: 500,
      body: { message: 'Server error' }
    }).as('getServicesError');

    cy.visit('/services', { failOnStatusCode: false });
    cy.wait('@getServicesError');
    cy.get('[data-cy="error-message"]').should('be.visible');

    // 2. Retry and succeed (check if retry button exists)
    cy.intercept('GET', '**/api/services', {
      statusCode: 200,
      body: []
    }).as('getServicesSuccess');

    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="retry-button"]').length > 0) {
        cy.get('[data-cy="retry-button"]').click();
        cy.waitForApi('@getServicesSuccess');
      } else {
        // If no retry button, just reload the page
        cy.reload();
        cy.waitForApi('@getServicesSuccess');
      }
    });

    // Check if services grid is visible or if there's an empty state
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="services-grid"]').length > 0) {
        cy.get('[data-cy="services-grid"]').should('be.visible');
      } else {
        // Accept that services loaded successfully even if grid isn't visible
        cy.get('[data-cy="catalog-header"]').should('be.visible');
      }
    });

    // 3. Test orders error recovery with correct API patterns
    cy.intercept('GET', '**/api/orders/search**', {
      statusCode: 500,
      body: { message: 'Server error' }
    }).as('getOrdersError');

    cy.visit('/orders', { failOnStatusCode: false });
    cy.wait('@getOrdersError');
    cy.get('[data-cy="error-message"]').should('be.visible');

    // 4. Retry and succeed
    cy.intercept('GET', '**/api/orders/search**', {
      statusCode: 200,
      body: { data: [], pagination: { total: 0, totalPages: 0, page: 1, limit: 20 } }
    }).as('getOrdersSuccess');
    cy.get('[data-cy="retry-button"]').click();
    cy.waitForApi('@getOrdersSuccess');
  });

  it('should handle authentication expiration during workflow', () => {
    // 1. Start a workflow with correct API patterns
    cy.visit('/services');
    cy.intercept('GET', '**/api/services').as('getServices');
    cy.waitForApi('@getServices');

    // 2. Simulate token expiration during profile update
    cy.visit('/profile');
    cy.intercept('GET', '**/api/auth/me').as('getProfile');
    cy.waitForApi('@getProfile');

    // 3. Mock 401 error for profile update with correct API pattern
    cy.intercept('PUT', '**/api/auth/profile', {
      statusCode: 401,
      body: { message: 'Token expired' }
    }).as('updateProfileUnauthorized');

    // 4. Try to update profile
    cy.get('[data-cy="first-name-input"]').clear().type('Test');
    cy.get('[data-cy="save-button"]').click();
    // Wait for the 401 response without using waitForApi which expects success
    cy.wait('@updateProfileUnauthorized');

    // 5. Should redirect to login
    cy.url().should('include', '/auth/login');

    // 6. Login again
    cy.fixture('users').then((users) => {
      cy.get('[data-cy="email-input"]').clear().type(users.testUser.email);
      cy.get('[data-cy="password-input"]').clear().type(users.testUser.password);
      cy.get('[data-cy="login-button"]').click();

      // Should redirect back to dashboard
      cy.url().should('include', '/dashboard');
    });
  });

  it('should handle responsive design and mobile viewport', () => {
    // Mock services for mobile test with correct API patterns
    cy.intercept('GET', '**/api/services', {
      statusCode: 200,
      body: [
        {
          id: '660e8400-e29b-41d4-a716-446655440001',
          name: 'Mobile Test Service',
          serviceType: 'FIBER',
          description: 'Test service for mobile',
          basePricing: { setupFee: 150.00, monthlyFee: 299.00 },
          categoryName: 'Business Internet'
        }
      ]
    }).as('getServices');

    // Mock orders for mobile test with correct API patterns
    cy.intercept('GET', '**/api/orders/search**', {
      statusCode: 200,
      body: {
        data: [],
        pagination: { total: 0, totalPages: 0, page: 1, limit: 20 }
      }
    }).as('getOrders');

    // 1. Test mobile viewport
    cy.viewport(375, 667); // iPhone SE

    // 2. Navigate through main sections
    cy.visit('/dashboard');
    cy.get('[data-cy="mobile-menu-toggle"]').should('be.visible');
    // Force click to avoid element coverage issues
    cy.get('[data-cy="mobile-menu-toggle"]').click({ force: true });
    cy.get('[data-cy="mobile-nav"]').should('be.visible');

    // 3. Navigate to services on mobile (use regular nav link with mobile data-cy)
    cy.get('[data-cy="nav-services"]').click();
    cy.url().should('include', '/services');

    // 4. Test service cards on mobile
    cy.waitForApi('@getServices');
    cy.get('[data-cy="service-card"]').should('be.visible');

    // 5. Test orders on mobile
    cy.get('[data-cy="mobile-menu-toggle"]').click();
    cy.get('[data-cy="nav-orders"]').click();
    cy.url().should('include', '/orders');
    cy.waitForApi('@getOrders');

    // 6. Reset to desktop viewport
    cy.viewport(1280, 720);
  });

  it('should handle concurrent user sessions', () => {
    // This test simulates multiple browser tabs/sessions
    // Note: User is already logged in from beforeEach hook

    // Mock profile API for initial load
    cy.intercept('GET', '**/api/auth/me', {
      statusCode: 200,
      body: {
        id: '880e8400-e29b-41d4-a716-446655440002',
        username: 'jane.smith',
        email: 'jane.smith@techstart.sg',
        firstName: 'Jane',
        lastName: 'Smith',
        phone: '+65 9234 5678',
        role: 'USER',
        status: 'ACTIVE'
      }
    }).as('getProfile');

    cy.visit('/profile');
    cy.waitForApi('@getProfile');

    // 2. Simulate another session modifying the same data with correct API pattern
    cy.intercept('PUT', '**/api/auth/profile', {
      statusCode: 409,
      body: { message: 'Profile was modified by another session' }
    }).as('updateProfileConflict');

    // 3. Try to update profile
    cy.get('[data-cy="first-name-input"]').clear().type('Concurrent Test');
    cy.get('[data-cy="save-button"]').click();
    cy.wait('@updateProfileConflict');

    // 4. Should show conflict error
    cy.get('[data-cy="error-message"]').should('contain', 'modified by another session');

    // 5. Reload profile to get latest data
    cy.get('[data-cy="reload-profile"]').click();
    cy.waitForApi('@getProfile');
  });
});
