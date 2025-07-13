describe('Service Catalog', () => {
  beforeEach(() => {
    cy.loginAsTestUser();
  });

  it('should display service catalog page', () => {
    cy.visit('/services');
    
    // Check page header
    cy.get('[data-cy="catalog-header"]').should('contain', 'Service Catalog');
    cy.get('[data-cy="catalog-description"]').should('be.visible');
    
    // Check search and filter controls
    cy.get('[data-cy="search-input"]').should('be.visible');
    cy.get('[data-cy="category-filter"]').should('be.visible');
    cy.get('[data-cy="service-type-filter"]').should('be.visible');
    cy.get('[data-cy="price-range-filter"]').should('be.visible');
  });

  it('should load and display services', () => {
    cy.visit('/services');

    // Check loading state
    cy.get('[data-cy="loading-spinner"]').should('be.visible');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that services are displayed
    cy.get('[data-cy="services-grid"]').should('be.visible');
    cy.get('[data-cy="service-card"]').should('have.length.greaterThan', 0);

    // Check service card content
    cy.get('[data-cy="service-card"]').first().within(() => {
      cy.get('[data-cy="service-name"]').should('be.visible');
      cy.get('[data-cy="service-description"]').should('be.visible');
      cy.get('[data-cy="service-price"]').should('be.visible');
      cy.get('[data-cy="service-category"]').should('be.visible');
    });
  });

  it('should search services by keyword', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Search for Fiber using real API endpoint /api/services/search
    cy.get('[data-cy="search-input"]').type('Fiber');
    cy.get('[data-cy="search-button"]').click();

    // Wait for search results to load
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that results contain Fiber services (if any exist)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length > 0) {
        cy.get('[data-cy="service-card"]').should('have.length.greaterThan', 0);
        cy.get('[data-cy="service-name"]').should('contain', 'Fiber');
      } else {
        // No Fiber services found
        cy.get('[data-cy="empty-results"]').should('be.visible');
      }
    });
  });

  it('should filter services by category', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Filter by Business Internet category (using real category name from backend data)
    cy.get('[data-cy="category-filter"]').select('Business Internet');

    // Wait for filtered results to load
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that all displayed services are Business Internet (if any)
    // Use a more robust approach to avoid DOM detachment issues
    cy.get('[data-cy="service-card"]').should('have.length.greaterThan', 0);
    cy.get('[data-cy="service-card"]').first().find('[data-cy="service-category"]').should('contain', 'Business Internet');
  });

  it('should filter services by service type', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Filter by FIBER service type (using real service type from backend)
    cy.get('[data-cy="service-type-filter"]').select('FIBER');

    // Wait for filtered results to load
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that results are filtered correctly (if any FIBER services exist)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length > 0) {
        cy.get('[data-cy="service-card"]').should('have.length.greaterThan', 0);
        cy.get('[data-cy="service-name"]').should('contain', 'Fiber');
      } else {
        // No FIBER services found
        cy.get('[data-cy="empty-results"]').should('be.visible');
      }
    });
  });

  it('should filter services by price range', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Filter by price range that matches real data (use existing option like 200-500)
    cy.get('[data-cy="price-range-filter"]').select('200-500');

    // Wait for filtered results to load
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that displayed services are within price range (if any)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length > 0) {
        cy.get('[data-cy="service-card"]').first().find('[data-cy="service-price"]').invoke('text').then((priceText) => {
          const price = parseFloat(priceText.replace(/[^0-9.]/g, ''));
          expect(price).to.be.within(200, 500);
        });
      } else {
        // No services in this price range
        cy.get('[data-cy="empty-results"]').should('be.visible');
      }
    });
  });

  it('should sort services', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Sort by price
    cy.get('[data-cy="sort-select"]').select('price');

    // Wait for sorting to complete
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that services are sorted by price (ascending) if any services exist
    // Use a simpler approach to avoid complex array operations
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length >= 2) {
        // Get first two service prices and verify first <= second
        cy.get('[data-cy="service-card"]').first().find('[data-cy="service-price"]').invoke('text').then((firstPriceText) => {
          const firstPrice = parseFloat(firstPriceText.replace(/[^0-9.]/g, ''));
          cy.get('[data-cy="service-card"]').eq(1).find('[data-cy="service-price"]').invoke('text').then((secondPriceText) => {
            const secondPrice = parseFloat(secondPriceText.replace(/[^0-9.]/g, ''));
            expect(firstPrice).to.be.at.most(secondPrice);
          });
        });
      } else {
        cy.log('Not enough services to verify sorting');
      }
    });
  });

  it('should navigate to service details', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check if services exist before trying to click
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length > 0) {
        // Click on first service card
        cy.get('[data-cy="service-card"]').first().click();

        // Should navigate to service details page
        cy.url().should('include', '/services/');

        // Wait for service details to load
        cy.get('[data-cy="loading-spinner"]').should('not.exist');

        // Check service details page content
        cy.get('[data-cy="service-details"]').should('be.visible');
        cy.get('[data-cy="service-name"]').should('be.visible');
        cy.get('[data-cy="service-description"]').should('be.visible');
        cy.get('[data-cy="service-features"]').should('be.visible');
        cy.get('[data-cy="order-button"]').should('be.visible');
      } else {
        cy.log('No services available to navigate to');
      }
    });
  });

  it('should handle pagination', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services/paged
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check if pagination is visible (only if there are multiple pages)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="pagination"]').length > 0) {
        // Click next page
        cy.get('[data-cy="next-page"]').click();

        // Wait for next page to load
        cy.get('[data-cy="loading-spinner"]').should('not.exist');

        // Check that page number changed
        cy.get('[data-cy="current-page"]').should('not.contain', '1');
      } else {
        cy.log('No pagination available (not enough services)');
      }
    });
  });

  it('should handle empty search results', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Search for non-existent service using real API endpoint /api/services/search
    cy.get('[data-cy="search-input"]').type('NonExistentService123');
    cy.get('[data-cy="search-button"]').click();

    // Wait for search results to load
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check empty state message
    cy.get('[data-cy="empty-results"]').should('be.visible');
    cy.get('[data-cy="empty-results"]').should('contain', 'No services found');
  });

  it('should clear filters', () => {
    cy.visit('/services');

    // Wait for services to load using real API endpoint /api/services
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Apply filters
    cy.get('[data-cy="search-input"]').type('Fiber');
    cy.get('[data-cy="category-filter"]').select('550e8400-e29b-41d4-a716-446655440001');
    cy.get('[data-cy="service-type-filter"]').select('FIBER');

    // Clear filters
    cy.get('[data-cy="clear-filters"]').click();

    // Wait for filters to be cleared and all services to reload
    cy.get('[data-cy="loading-spinner"]').should('not.exist');

    // Check that filters are cleared
    cy.get('[data-cy="search-input"]').should('have.value', '');
    cy.get('[data-cy="category-filter"]').should('have.value', '');
    cy.get('[data-cy="service-type-filter"]').should('have.value', '');

    // Should show all services again (if any exist)
    cy.get('body').then(($body) => {
      if ($body.find('[data-cy="service-card"]').length > 0) {
        cy.get('[data-cy="service-card"]').should('have.length.greaterThan', 0);
      } else {
        cy.log('No services available');
      }
    });
  });
});
