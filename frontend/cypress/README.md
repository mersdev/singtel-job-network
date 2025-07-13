# Cypress E2E Testing for Singtel Business Network On-Demand

This directory contains comprehensive end-to-end tests for the Singtel Business Network On-Demand frontend application using Cypress.

## Test Structure

```
cypress/
├── e2e/                    # Test specifications
│   ├── auth.cy.ts         # Authentication flow tests
│   ├── profile.cy.ts      # Profile management tests
│   ├── service-catalog.cy.ts # Service catalog tests
│   ├── orders.cy.ts       # Order management tests
│   └── integration.cy.ts  # End-to-end integration tests
├── fixtures/              # Test data
│   ├── users.json         # User test data
│   └── services.json      # Service test data
├── support/               # Support files
│   ├── commands.ts        # Custom Cypress commands
│   └── e2e.ts            # Global configuration
├── scripts/               # Utility scripts
│   └── run-tests.sh      # Test execution script
└── README.md             # This file
```

## Test Coverage

### 1. Authentication Tests (`auth.cy.ts`)
- ✅ Login with valid credentials
- ✅ Login with invalid credentials
- ✅ Logout functionality
- ✅ Redirect to return URL after login
- ✅ Session expiration handling
- ✅ Unauthenticated access protection

### 2. Profile Management Tests (`profile.cy.ts`)
- ✅ Display user profile information
- ✅ Update profile information
- ✅ Form validation (required fields, email format)
- ✅ Reset form functionality
- ✅ Error handling for profile operations
- ✅ Authentication error handling

### 3. Service Catalog Tests (`service-catalog.cy.ts`)
- ✅ Display service catalog page
- ✅ Load and display services
- ✅ Search services by keyword
- ✅ Filter services by category
- ✅ Filter services by service type
- ✅ Filter services by price range
- ✅ Sort services
- ✅ Navigate to service details
- ✅ Handle pagination
- ✅ Handle empty search results
- ✅ Error handling

### 4. Order Management Tests (`orders.cy.ts`)
- ✅ Display orders list page
- ✅ Load and display orders
- ✅ Create new order
- ✅ View order details
- ✅ Cancel order
- ✅ Filter orders by status
- ✅ Filter orders by order type
- ✅ Filter orders by date range
- ✅ Search orders
- ✅ Handle pagination
- ✅ Error handling

### 5. Integration Tests (`integration.cy.ts`)
- ✅ Complete order workflow
- ✅ User profile update workflow
- ✅ Navigation between sections
- ✅ Error recovery scenarios
- ✅ Authentication expiration during workflow
- ✅ Responsive design testing
- ✅ Concurrent user sessions

## Prerequisites

1. **Backend Running**: Spring Boot backend must be running on port 8088
2. **Frontend Running**: Angular frontend must be running on port 4200
3. **Test Data**: Database should be populated with sample data
4. **Node.js**: Node.js 18+ and npm 9+

## Quick Start

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Start Services
```bash
# Terminal 1: Start backend
cd backend
./mvnw spring-boot:run

# Terminal 2: Start frontend
cd frontend
npm start
```

### 3. Run Tests
```bash
# Run all tests (headless)
npm run e2e

# Open Cypress Test Runner (interactive)
npm run e2e:open

# Run specific test file
npx cypress run --spec "cypress/e2e/auth.cy.ts"

# Run with specific browser
npx cypress run --browser firefox
```

## Test Execution Options

### Using npm Scripts
```bash
# Headless execution
npm run e2e
npm run cypress:run

# Interactive mode
npm run e2e:open
npm run cypress:open

# Headless with specific browser
npm run e2e:headless -- --browser firefox
```

### Using the Test Script
```bash
# Run all tests in headless mode
./cypress/scripts/run-tests.sh --headless

# Run specific test spec
./cypress/scripts/run-tests.sh --spec "cypress/e2e/auth.cy.ts"

# Run tests with specific browser
./cypress/scripts/run-tests.sh --browser firefox

# Open Cypress Test Runner
./cypress/scripts/run-tests.sh --open

# Run against different environment
./cypress/scripts/run-tests.sh \
  --backend-url "https://api.staging.singtel.com" \
  --frontend-url "https://staging.singtel.com"
```

## Test Data

### User Accounts
```json
{
  "testUser": {
    "email": "user@company.com",
    "password": "user123"
  },
  "adminUser": {
    "email": "admin@singtel.com", 
    "password": "admin123"
  }
}
```

### Sample Services
- MPLS Network Service
- Internet Connectivity
- VPN Gateway

## Custom Commands

### Authentication Commands
```typescript
// Login with credentials
cy.login('user@company.com', 'password123');

// Login as test user
cy.loginAsTestUser();

// Login as admin
cy.loginAsAdmin();

// Logout
cy.logout();

// Check if authenticated
cy.checkAuthenticated();
```

### API Commands
```typescript
// Wait for API response
cy.waitForApi('@getServices');
```

## Data Attributes

All interactive elements use `data-cy` attributes for reliable test selection:

### Authentication
- `data-cy="login-form"`
- `data-cy="email-input"`
- `data-cy="password-input"`
- `data-cy="login-button"`
- `data-cy="error-message"`

### Profile
- `data-cy="profile-form"`
- `data-cy="first-name-input"`
- `data-cy="last-name-input"`
- `data-cy="email-input"`
- `data-cy="phone-input"`
- `data-cy="save-button"`
- `data-cy="reset-button"`
- `data-cy="success-message"`

### Navigation
- `data-cy="nav-dashboard"`
- `data-cy="nav-services"`
- `data-cy="nav-orders"`
- `data-cy="nav-profile"`
- `data-cy="user-menu"`
- `data-cy="logout-button"`

### Service Catalog
- `data-cy="search-input"`
- `data-cy="category-filter"`
- `data-cy="service-card"`
- `data-cy="service-name"`

### Orders
- `data-cy="orders-table"`
- `data-cy="order-row"`
- `data-cy="new-order-button"`
- `data-cy="view-button"`
- `data-cy="cancel-button"`

## Configuration

### Environment Variables
```typescript
// cypress.config.ts
env: {
  apiUrl: 'http://localhost:8088/api',
  testUser: {
    email: 'user@company.com',
    password: 'user123'
  },
  adminUser: {
    email: 'admin@singtel.com',
    password: 'admin123'
  }
}
```

### Base Configuration
```typescript
// cypress.config.ts
e2e: {
  baseUrl: 'http://localhost:4200',
  viewportWidth: 1280,
  viewportHeight: 720,
  video: false,
  screenshotOnRunFailure: true,
  defaultCommandTimeout: 10000
}
```

## CI/CD Integration

### GitHub Actions Example
```yaml
name: E2E Tests
on: [push, pull_request]

jobs:
  e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
      
      - name: Install dependencies
        run: npm ci
        working-directory: frontend
      
      - name: Start backend
        run: |
          cd backend
          ./mvnw spring-boot:run &
          sleep 30
      
      - name: Start frontend
        run: |
          cd frontend
          npm start &
          sleep 30
      
      - name: Run Cypress tests
        run: |
          cd frontend
          npm run e2e:headless
```

### Docker Integration
```dockerfile
# Dockerfile.cypress
FROM cypress/included:latest
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
CMD ["npm", "run", "e2e:headless"]
```

## Debugging

### Debug Mode
```bash
# Run with debug output
DEBUG=cypress:* npm run e2e

# Run with browser console logs
npx cypress run --browser chrome --headed
```

### Screenshots and Videos
- Screenshots are automatically taken on test failures
- Videos can be enabled in `cypress.config.ts`
- Artifacts are saved in `cypress/screenshots/` and `cypress/videos/`

### Browser DevTools
```typescript
// Add debugger in test
it('should debug test', () => {
  cy.visit('/dashboard');
  cy.debug(); // Opens DevTools
  cy.pause(); // Pauses execution
});
```

## Best Practices

1. **Use data-cy attributes** for element selection
2. **Wait for API responses** using `cy.waitForApi()`
3. **Clean up state** between tests
4. **Use custom commands** for common operations
5. **Test error scenarios** and edge cases
6. **Keep tests independent** and atomic
7. **Use meaningful test descriptions**
8. **Mock external dependencies** when needed

## Troubleshooting

### Common Issues

1. **Element not found**
   ```typescript
   // Wait for element to be visible
   cy.get('[data-cy="element"]').should('be.visible');
   ```

2. **API timing issues**
   ```typescript
   // Increase timeout for slow APIs
   cy.waitForApi('@slowApi', { timeout: 30000 });
   ```

3. **Authentication issues**
   ```typescript
   // Clear storage before login
   cy.window().then((win) => {
     win.localStorage.clear();
   });
   ```

4. **Flaky tests**
   ```typescript
   // Add explicit waits
   cy.wait(1000);
   cy.get('[data-cy="element"]').should('exist');
   ```

## Contributing

1. Follow the existing test structure and naming conventions
2. Add data-cy attributes to new UI elements
3. Write comprehensive test descriptions
4. Include both positive and negative test cases
5. Update this documentation for new test files
6. Ensure tests are independent and can run in any order

## Support

For issues or questions about the test suite:
1. Check the troubleshooting section
2. Review Cypress documentation
3. Contact the development team
