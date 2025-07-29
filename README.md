# Expense Tracker

A production-grade Spring Boot REST API for personal expense tracking and budget management. Features JWT authentication, role-based access control, input validation, pagination, and comprehensive error handling.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.4.1 (Java 17) |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8+ / H2 (tests) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Bean Validation (Jakarta) |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Build | Maven |
| Testing | JUnit 5, Mockito, MockMvc |

## Architecture

```
com.tracker.expense_tracker/
├── config/          # Security, OpenAPI configuration
├── controller/      # REST controllers (Auth, User, Expense, Budget)
├── dto/
│   ├── request/     # Validated request DTOs
│   └── response/    # Response DTOs with factory methods
├── entity/          # JPA entities with Lombok
├── exception/       # Custom exceptions + @ControllerAdvice handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT service, authentication filter
└── service/         # Business logic layer
```

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### Users (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user profile |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users` | Get all users |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Expenses (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Add new expense |
| GET | `/api/expenses` | Get expenses (paginated, sorted) |
| GET | `/api/expenses/all` | Get all expenses |
| PUT | `/api/expenses/{id}` | Update expense |
| DELETE | `/api/expenses/{id}` | Delete expense |

### Budgets (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/budgets` | Create monthly budget |
| GET | `/api/budgets` | Get all budgets |
| GET | `/api/budgets/{month}/{year}` | Get budget for month/year |
| PUT | `/api/budgets/{id}` | Update budget |
| DELETE | `/api/budgets/{id}` | Delete budget |

## Key Features

- **JWT Authentication** — Stateless token-based auth with BCrypt password hashing
- **Request Validation** — `@Valid` on all endpoints with structured error responses
- **DTO Pattern** — Clean separation between API layer and persistence layer
- **Global Exception Handling** — `@ControllerAdvice` with consistent `ApiResponse<T>` envelope
- **Pagination & Sorting** — Pageable support on list endpoints
- **Auto Budget Sync** — Adding/updating/deleting expenses auto-recalculates budget totals
- **Resource Ownership** — Users can only access their own expenses and budgets
- **Swagger UI** — Interactive API docs at `/swagger-ui.html`
- **Actuator** — Health and metrics at `/actuator/health`

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8+ running on `localhost:3306`
- Database `expense-tracker-db` must exist

### Build and Run

```bash
# Build
mvn clean package

# Run
java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar

# Or run directly
mvn spring-boot:run
```

### Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense-tracker-db
spring.datasource.username=root
spring.datasource.password=<your-password>
jwt.secret=<your-base64-secret>
```

### Run Tests

```bash
mvn test
```

Tests use H2 in-memory database — no MySQL required.

### API Documentation

Start the application and visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Sample Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"John","email":"john@example.com","password":"password123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

### Add Expense (with JWT)
```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"amount":45.50,"category":"Food","description":"Lunch","expenseDate":"2025-12-15"}'
```

## Test Coverage

| Test Class | Tests | Type |
|------------|-------|------|
| AuthServiceTest | 2 | Unit |
| ExpenseServiceTest | 5 | Unit |
| BudgetServiceTest | 4 | Unit |
| AuthControllerTest | 3 | Integration |
| ExpenseTrackerApplicationTests | 1 | Smoke |
| **Total** | **15** | |
