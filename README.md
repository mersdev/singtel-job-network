# Singtel Business Network On-Demand (MVP)

A self-service web portal that enables SMEs to instantly order, configure, and monitor their essential network services.

## Project Structure

```
singtel-job-network/
├── frontend/                 # Angular frontend application
├── backend/                  # Spring Boot backend application
├── database/                 # PostgreSQL schema and migrations
├── docker-compose.yml        # Development environment setup
├── Taskfile.yml             # Task runner for database operations
└── README.md                # This file
```

## Technology Stack

### Frontend
- **Framework**: Angular 17+ with Vite
- **Styling**: Tailwind CSS
- **Build Tool**: Vite
- **UI Components**: Angular Material (optional)

### Backend
- **Framework**: Spring Boot 3.x with Java 17+
- **Database**: PostgreSQL 15+
- **Security**: Spring Security with JWT
- **API Documentation**: OpenAPI/Swagger

### Database
- **Database**: PostgreSQL 15+
- **Migration Tool**: Flyway (integrated with Spring Boot)
- **Task Runner**: Taskfile for database operations

## Features (MVP)

1. **Self-Service Customer Portal & Service Catalog**
   - Secure authentication and user management
   - Dashboard with current services overview
   - Service catalog browsing

2. **Automated Service Provisioning**
   - Order placement for network services
   - Automated provisioning workflows
   - Order status tracking

3. **On-Demand Bandwidth Management**
   - Dynamic bandwidth adjustment
   - Real-time configuration changes
   - Cost optimization features

4. **Basic Service Monitoring Dashboard**
   - Real-time service status monitoring
   - Bandwidth utilization metrics
   - Uptime statistics

## Getting Started

### Prerequisites
- Node.js 18+ and npm
- Java 17+
- PostgreSQL 15+
- Docker and Docker Compose (optional)

### Development Setup

1. **Database Setup**
   ```bash
   # Using Taskfile
   task db:setup
   task db:migrate
   ```

2. **Backend Setup**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

### Using Docker Compose
```bash
docker-compose up -d
```

## API Documentation

Once the backend is running, API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Development Workflow

1. Database changes: Update schema in `database/migrations/`
2. Backend changes: Implement in `backend/src/main/java/`
3. Frontend changes: Implement in `frontend/src/app/`
4. Run tests: `task test:all`

## Contributing

1. Follow the established code structure
2. Write tests for new features
3. Update documentation as needed
4. Follow conventional commit messages

## License

Internal Singtel project - All rights reserved.
