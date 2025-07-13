# Singtel Business Network On-Demand

> A modern self-service web portal that enables SMEs to instantly order, configure, and monitor their essential network services.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17+-red.svg)](https://angular.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Vite](https://img.shields.io/badge/Vite-5+-purple.svg)](https://vitejs.dev/)

## 🚀 Quick Start

### Prerequisites

- **Java 17+** - Backend runtime
- **Node.js 18+** - Frontend development
- **Podman** - Container management (preferred over Docker)
- **Task** - Task runner for database operations

### 1. Clone and Setup

```bash
git clone <repository-url>
cd singtel-job-network
```

### 2. Database Setup

```bash
# Start PostgreSQL container with Podman
task db:setup

# Run database migrations
task db:migrate
```

### 3. Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend will be available at: http://localhost:8088

### 4. Start Frontend

```bash
cd frontend
npm install
npm start
```

The frontend will be available at: http://localhost:4200

## 📁 Project Structure

```
singtel-job-network/
├── 📁 backend/                    # Spring Boot REST API
│   ├── 📁 src/main/java/         # Java source code
│   ├── 📁 src/main/resources/    # Configuration & migrations
│   ├── 📁 src/test/              # Unit & integration tests
│   ├── 📄 pom.xml                # Maven dependencies
│   └── 📄 Dockerfile             # Container configuration
├── 📁 frontend/                   # Angular web application
│   ├── 📁 src/app/               # Angular components & services
│   ├── 📁 cypress/               # E2E tests
│   ├── 📄 package.json           # Node.js dependencies
│   ├── 📄 vite.config.ts         # Vite build configuration
│   └── 📄 tailwind.config.js     # Tailwind CSS configuration
├── 📄 Taskfile.yml               # Database & development tasks
├── 📄 .gitignore                 # Git ignore rules
└── 📄 README.md                  # This file
```

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.2+ with Java 17+
- **Database**: PostgreSQL 15+ (port 5632)
- **Security**: Spring Security with JWT authentication
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Testcontainers, Zonky Embedded Database
- **Migration**: Flyway for database schema management
- **Build**: Maven with Spring Boot Maven Plugin

### Frontend
- **Framework**: Angular 17+ with Standalone Components
- **Build Tool**: Vite (instead of Angular CLI webpack)
- **Styling**: Tailwind CSS with responsive design
- **Charts**: Chart.js with ng2-charts
- **State Management**: Angular Signals
- **Testing**: Jasmine, Karma, Cypress for E2E
- **Code Quality**: ESLint, Prettier

### Infrastructure
- **Container**: Podman (preferred) or Docker
- **Database**: PostgreSQL 15+ in container
- **Task Runner**: Taskfile for database operations
- **Development**: Hot reload for both frontend and backend

## ✨ Features

### 🔐 Authentication & Security
- JWT-based authentication with automatic token refresh
- Role-based access control (RBAC)
- Secure API endpoints with Spring Security
- Password encryption with BCrypt

### 📊 Service Catalog Management
- Browse and search network services
- Service categorization and filtering
- Dynamic pricing and bandwidth options
- Featured and popular services

### 📋 Order Management
- Complete order lifecycle management
- Real-time order status tracking
- Order history and analytics
- Automated provisioning workflows

### 👤 User Management
- User profile management
- Company-based user organization
- Admin panel for user administration
- Profile settings and preferences

### 📈 Dashboard & Monitoring
- Real-time service monitoring
- Order statistics and analytics
- Bandwidth utilization metrics
- Service uptime tracking

## 🔧 Development

### Database Operations

```bash
# Setup database container
task db:setup

# Run migrations
task db:migrate

# Reset database (clean + migrate)
task db:reset

# Connect to database
task db:connect

# View migration status
task db:info

# Stop database
task db:stop
```

### Backend Development

```bash
cd backend

# Run application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run unit tests
npm test

# Run E2E tests
npm run e2e

# Lint code
npm run lint
```

## 🌐 API Documentation

### Swagger UI
Once the backend is running, comprehensive API documentation is available at:
- **Swagger UI**: http://localhost:8088/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8088/v3/api-docs

### Key Endpoints

#### Authentication
```
POST /api/auth/login          # User login
POST /api/auth/refresh        # Refresh JWT token
GET  /api/auth/me            # Get current user profile
POST /api/auth/logout        # User logout
```

#### Service Catalog
```
GET  /api/services                    # List all services
GET  /api/services/search            # Search services
GET  /api/services/categories        # Get service categories
GET  /api/services/popular           # Get popular services
GET  /api/services/bandwidth-adjustable  # Get bandwidth adjustable services
```

#### Order Management
```
POST /api/orders                     # Create new order
GET  /api/orders/paged              # Get paginated orders
GET  /api/orders/{id}               # Get order by ID
POST /api/orders/{id}/cancel        # Cancel order
GET  /api/orders/statistics         # Get order statistics
```

## 🧪 Testing

### Backend Testing
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run integration tests
./mvnw test -Dtest=**/*IntegrationTest

# Generate test coverage report
./mvnw test jacoco:report
```

### Frontend Testing
```bash
cd frontend

# Unit tests
npm test                    # Run once
npm run test:watch         # Watch mode
npm run test:coverage      # With coverage

# E2E tests
npm run e2e                # Headless mode
npm run e2e:open          # Interactive mode
```

## 🚀 Deployment

### Environment Configuration

#### Backend (application.yml)
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5632}/${DB_NAME:singtel_network_db}
    username: ${DB_USER:singtel_user}
    password: ${DB_PASSWORD:singtel_password}

app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key}
    expiration: ${JWT_EXPIRATION:86400000}
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:4200}
```

#### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8088/api',
  appName: 'Singtel Business Network',
  version: '1.0.0'
};
```

### Production Build

#### Backend
```bash
cd backend
./mvnw clean package -Pprod
java -jar target/backend-1.0.0.jar --spring.profiles.active=prod
```

#### Frontend
```bash
cd frontend
npm run build
# Serve dist/ folder with your web server
```

## 🔍 Monitoring & Health Checks

### Application Health
```bash
# Backend health check
curl http://localhost:8088/actuator/health

# Database connectivity
curl http://localhost:8088/actuator/health/db

# Application metrics
curl http://localhost:8088/actuator/metrics
```

## 🛠 Troubleshooting

### Common Issues

#### Database Connection
```bash
# Check if PostgreSQL container is running
podman ps | grep singtel-postgres

# Restart database
task db:restart

# Check database logs
task db:logs
```

#### Backend Issues
```bash
# Check application logs
tail -f backend/logs/application.log

# Verify JWT configuration
grep JWT backend/src/main/resources/application.yml
```

#### Frontend Issues
```bash
# Clear node modules and reinstall
rm -rf frontend/node_modules frontend/package-lock.json
cd frontend && npm install

# Check for TypeScript errors
cd frontend && npm run build
```

## 📝 Development Guidelines

### Code Standards
- **Java**: Follow Spring Boot best practices and Google Java Style Guide
- **TypeScript**: Use Angular style guide and strict TypeScript configuration
- **Testing**: Maintain >80% code coverage for both backend and frontend
- **Documentation**: Update API documentation for new endpoints

### Git Workflow
1. Create feature branch from `main`
2. Follow conventional commit messages
3. Write comprehensive tests
4. Update documentation
5. Submit pull request with detailed description

### Commit Message Format
```
type(scope): description

feat(auth): add JWT token refresh functionality
fix(orders): resolve order status update issue
docs(readme): update setup instructions
test(services): add unit tests for service catalog
```

## 📄 License

**Internal Singtel Project** - All rights reserved.

---

## 🤝 Contributing

This is an internal Singtel project. For questions or contributions, please contact the development team.

### Development Team
- **Backend**: Spring Boot & Java development
- **Frontend**: Angular & TypeScript development
- **DevOps**: Container orchestration & deployment
- **QA**: Testing & quality assurance

---

*Last updated: 2025-01-13*
