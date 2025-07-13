#!/bin/bash

# Cypress Test Execution Script for Singtel Business Network On-Demand
# This script provides various options for running Cypress tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
BACKEND_URL="http://localhost:8088"
FRONTEND_URL="http://localhost:4200"
BROWSER="chrome"
HEADLESS=false
SPEC=""
RECORD=false
PARALLEL=false

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if service is running
check_service() {
    local url=$1
    local service_name=$2
    
    print_status "Checking if $service_name is running at $url..."
    
    if curl -s --head "$url" | head -n 1 | grep -q "200 OK\|302"; then
        print_success "$service_name is running"
        return 0
    else
        print_error "$service_name is not running at $url"
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s --head "$url" | head -n 1 | grep -q "200 OK\|302"; then
            print_success "$service_name is ready"
            return 0
        fi
        
        print_status "Attempt $attempt/$max_attempts - $service_name not ready yet..."
        sleep 2
        ((attempt++))
    done
    
    print_error "$service_name failed to start within expected time"
    return 1
}

# Function to display help
show_help() {
    cat << EOF
Cypress Test Execution Script

Usage: $0 [OPTIONS]

OPTIONS:
    -h, --help              Show this help message
    -b, --browser BROWSER   Browser to use (chrome, firefox, edge) [default: chrome]
    -s, --spec SPEC         Specific test spec to run
    -H, --headless          Run tests in headless mode
    -r, --record            Record tests to Cypress Dashboard
    -p, --parallel          Run tests in parallel
    -o, --open              Open Cypress Test Runner (interactive mode)
    --backend-url URL       Backend API URL [default: http://localhost:8088]
    --frontend-url URL      Frontend URL [default: http://localhost:4200]
    --skip-checks           Skip service availability checks

EXAMPLES:
    # Run all tests in headless mode
    $0 --headless

    # Run specific test spec
    $0 --spec "cypress/e2e/auth.cy.ts"

    # Run tests with specific browser
    $0 --browser firefox

    # Open Cypress Test Runner
    $0 --open

    # Run tests with recording (requires CYPRESS_RECORD_KEY)
    $0 --record --parallel

    # Run tests against different environment
    $0 --backend-url "https://api.staging.singtel.com" --frontend-url "https://staging.singtel.com"

ENVIRONMENT VARIABLES:
    CYPRESS_RECORD_KEY      Required for recording tests
    CYPRESS_PROJECT_ID      Required for recording tests
    CI                      Set to 'true' for CI environment

EOF
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -s|--spec)
            SPEC="$2"
            shift 2
            ;;
        -H|--headless)
            HEADLESS=true
            shift
            ;;
        -r|--record)
            RECORD=true
            shift
            ;;
        -p|--parallel)
            PARALLEL=true
            shift
            ;;
        -o|--open)
            OPEN=true
            shift
            ;;
        --backend-url)
            BACKEND_URL="$2"
            shift 2
            ;;
        --frontend-url)
            FRONTEND_URL="$2"
            shift 2
            ;;
        --skip-checks)
            SKIP_CHECKS=true
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Main execution
main() {
    print_status "Starting Cypress test execution..."
    
    # Check if we're in the correct directory
    if [ ! -f "cypress.config.ts" ]; then
        print_error "cypress.config.ts not found. Please run this script from the frontend directory."
        exit 1
    fi
    
    # Skip service checks if requested
    if [ "$SKIP_CHECKS" != "true" ]; then
        # Check if backend is running
        if ! check_service "$BACKEND_URL/actuator/health" "Backend API"; then
            print_warning "Backend API is not running. Some tests may fail."
            print_status "To start the backend, run: cd ../backend && ./mvnw spring-boot:run"
        fi
        
        # Check if frontend is running (only if not opening Cypress)
        if [ "$OPEN" != "true" ]; then
            if ! check_service "$FRONTEND_URL" "Frontend Application"; then
                print_error "Frontend application is not running."
                print_status "To start the frontend, run: npm start"
                exit 1
            fi
        fi
    fi
    
    # Build Cypress command
    CYPRESS_CMD="npx cypress"
    
    if [ "$OPEN" = "true" ]; then
        CYPRESS_CMD="$CYPRESS_CMD open"
    else
        CYPRESS_CMD="$CYPRESS_CMD run"
        
        if [ "$HEADLESS" = "true" ]; then
            CYPRESS_CMD="$CYPRESS_CMD --headless"
        fi
        
        CYPRESS_CMD="$CYPRESS_CMD --browser $BROWSER"
        
        if [ -n "$SPEC" ]; then
            CYPRESS_CMD="$CYPRESS_CMD --spec \"$SPEC\""
        fi
        
        if [ "$RECORD" = "true" ]; then
            if [ -z "$CYPRESS_RECORD_KEY" ]; then
                print_error "CYPRESS_RECORD_KEY environment variable is required for recording"
                exit 1
            fi
            CYPRESS_CMD="$CYPRESS_CMD --record"
            
            if [ "$PARALLEL" = "true" ]; then
                CYPRESS_CMD="$CYPRESS_CMD --parallel"
            fi
        fi
    fi
    
    # Set environment variables for Cypress
    export CYPRESS_baseUrl="$FRONTEND_URL"
    export CYPRESS_apiUrl="$BACKEND_URL/api"
    
    print_status "Executing: $CYPRESS_CMD"
    
    # Execute Cypress command
    eval $CYPRESS_CMD
    
    CYPRESS_EXIT_CODE=$?
    
    if [ $CYPRESS_EXIT_CODE -eq 0 ]; then
        print_success "All tests passed!"
    else
        print_error "Some tests failed. Exit code: $CYPRESS_EXIT_CODE"
        exit $CYPRESS_EXIT_CODE
    fi
}

# Run main function
main
