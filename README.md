# Expense Tracker

A full-stack expense tracking application with a Spring Boot REST API backend and React frontend. Features JWT authentication, role-based access control, input validation, interactive charts, and comprehensive testing.

## Project Structure

```
expense-tracker/
├── backend/          # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/com/tracker/expense_tracker/
│   │   │   ├── config/        # Security, OpenAPI, CORS
│   │   │   ├── controller/    # REST controllers
│   │   │   ├── dto/           # Request/Response DTOs
│   │   │   ├── entity/        # JPA entities
│   │   │   ├── exception/     # Global exception handler
│   │   │   ├── repository/    # Spring Data JPA
│   │   │   ├── security/      # JWT service + filter
│   │   │   └── service/       # Business logic
│   │   └── test/              # 49 tests (unit + integration + E2E)
│   └── pom.xml
├── frontend/         # React SPA
│   ├── src/
│   │   ├── api/        # Axios + JWT interceptor
│   │   ├── components/ # Layout, Toast, ProtectedRoute
│   │   ├── context/    # Auth state management
│   │   └── pages/      # Landing, Login, Register, Dashboard, Expenses, Budgets, Profile
│   └── package.json
└── README.md
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.4.1 (Java 17) |
| Frontend | React 18, Vite, Tailwind CSS |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8+ / H2 (tests) |
| ORM | Spring Data JPA / Hibernate |
| Charts | Recharts |
| Icons | Lucide React |
| Validation | Bean Validation (Jakarta) |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Testing | JUnit 5, Mockito, MockMvc |

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
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Expenses (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Add new expense |
| GET | `/api/expenses` | Get expenses (paginated) |
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

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8+ running on `localhost:3306`
- Database `expense-tracker-db` must exist

### Backend

```bash
cd backend
mvn spring-boot:run
```

Runs on `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:3000` (proxies API calls to backend)

### Configuration

Update `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense-tracker-db
spring.datasource.username=root
spring.datasource.password=<your-password>
jwt.secret=<your-base64-secret>
```

### Run Tests

```bash
cd backend
mvn test
```

49 tests pass using H2 in-memory database (no MySQL required).

### API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Key Features

- **JWT Authentication** with BCrypt password hashing
- **DTO Pattern** separating API from persistence
- **Global Exception Handling** with consistent API responses
- **Pagination and Sorting** on list endpoints
- **Auto Budget Sync** when expenses change
- **Resource Ownership** (users access only their own data)
- **Interactive Dashboard** with bar and pie charts
- **Dark/Light Mode** toggle
- **Responsive Design** (mobile + desktop)
- **Glassmorphism UI** with gradient accents

## Test Coverage

| Test Suite | Tests | Type |
|------------|-------|------|
| AuthServiceTest | 2 | Unit |
| ExpenseServiceTest | 5 | Unit |
| BudgetServiceTest | 4 | Unit |
| AuthControllerTest | 3 | Integration |
| FullFlowIntegrationTest | 34 | E2E |
| ExpenseTrackerApplicationTests | 1 | Smoke |
| **Total** | **49** | |
