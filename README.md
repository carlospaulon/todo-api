# TODO API - Spring Boot

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Coverage](https://img.shields.io/badge/Coverage-75%25-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

Complete task management system (To-Do List) with JWT authentication developed using Spring Boot.

---

## Index

- [About](#about)
- [Architecture](#project-architecture-pattern)
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running](#running)
- [Tests](#tests)
- [API Endpoints](#api-endpoints)
- [Swagger](#swagger)
- [License](#license)
- [Author](#author)

---

## About

REST API for task management with a complete authentication system, built as a study project for Spring Boot, Spring
Security, and JWT.

### Highlights:

- Stateless authentication with JWT
- Complete CRUD for tasks
- User data isolation
- Pagination, sorting, and advanced filters
- Robust validations with Bean Validation
- Automated tests (~75% coverage)
- Swagger/OpenAPI documentation
- Database migrations with Flyway

---

## Project Architecture Pattern

The project follows a **Layered Architecture**:

```
Controller (REST)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Database
```

### Applied Principles

- **Separation of Concerns (SoC)**  
  Each layer has a well-defined responsibility. Controllers handle HTTP concerns, services contain business logic, and
  repositories manage data access.

- **Single Responsibility Principle (SRP)**  
  Classes are focused on a single purpose, such as authentication handling, task management, or token validation.

- **Dependency Inversion Principle (DIP)**  
  High-level components depend on abstractions (interfaces), especially through Spring’s dependency injection.

- **DTO Pattern (Request / Response)**  
  Data Transfer Objects are used to decouple API contracts from persistence entities and avoid exposing internal models.

- **Repository Pattern**  
  Data access is abstracted using Spring Data JPA repositories.

---

## Features

### Authentication

- User registration with data validation
- Login with JWT token generation
- Route protection with JWT
- Secure password hashing with BCrypt
- Token validation on all protected requests

### Task Management

- Create tasks
- List tasks with pagination
- Get task by ID
- Update tasks (full or partial)
- Update status only
- Delete tasks
- Filter by status (PENDING, IN_PROGRESS, COMPLETED)
- Customizable sorting

### Security

- Authentication required for task operations
- Complete data isolation between users
- Resource ownership validation
- Input validation with Bean Validation
- Centralized exception handling
- SQL Injection protection (JPA/Hibernate)
- CORS configured for production

---

## Technologies

### Backend

- **Java 21** (LTS)
- **Spring Boot 3.2+**
- **Spring Security** (JWT)
- **Spring Data JPA** (Hibernate)
- **PostgreSQL 15+**
- **Flyway** (Migrations)
- **Maven**

### Libraries

- **Lombok** – Boilerplate reduction
- **JJWT 0.12.6** – JSON Web Tokens
- **SpringDoc OpenAPI 2.8.0** – Swagger documentation
- **Bean Validation** – Validations

### Testing

- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (tests)
- **JaCoCo** (coverage)

---

## Prerequisites

Before starting, you will need to have installed:

| Tool           | Version | Download Link                                                                                         |
|----------------|---------|-------------------------------------------------------------------------------------------------------|
| **Java JDK**   | 21+     | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/) |
| **PostgreSQL** | 15+     | [PostgreSQL Downloads](https://www.postgresql.org/download/)                                          |
| **Maven**      | 3.8+    | [Apache Maven](https://maven.apache.org/download.cgi) (or use the included wrapper)                   |
| **Git**        | Latest  | [Git Downloads](https://git-scm.com/downloads)                                                        |

### Installation Check

```bash
# Check Java version
java -version

# Check Maven (or use ./mvnw)
mvn -version

# Check PostgreSQL
psql --version

# Check Git
git --version

```

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/carlospaulon/todo-api.git
cd todo-api
```

### 2. Configure the database

Create a PostgreSQL database:

```bash
psql -U postgres
```

```sql
-- Create database
CREATE DATABASE todoapi_db;

-- Create user
CREATE USER todoapi_user WITH ENCRYPTED PASSWORD 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE todoapi_db TO todoapi_user;

-- Quit
\q
```

### 3. Configure credentials

Copy the example file:

```bash
cp src/main/resources/application-dev.yaml.example src/main/resources/application-dev.yaml
```

Edit the `application-dev.yaml` with your credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todoapi_db
    username: todoapi_user
    password: your_password

jwt:
  secret: generate_a_strong_secret_key_here_min_256_bits
  expiration: 86400000  # 24 hours
```

### 4. Generate JWT Secret Key (IMPORTANT!)

**Linux/Mac:**

```bash
openssl rand -base64 32
```

**Windows PowerShell:**

```powershell
# Run these commands in sequence:

$bytes = New-Object byte[] 32

[Security.Cryptography.RNGCryptoServiceProvider]::Create().GetBytes($bytes)

[Convert]::ToBase64String($bytes)

```

**Output example:**

```
8HGBjPWFWCnU2VjvgEgY3zb0D7x5kLTGrI7pslpcIMA=
```

Paste this value into `jwt.secret` in the `application-dev.yaml` file.


---

## Configuration

### Available Profiles

The project uses **Spring Profiles** for different environments.

| Profile | Usage             | Database         |
|---------|-------------------|------------------|
| `dev`   | Local development | PostgreSQL local |
| `test`  | Automated tests   | H2 (in-memory)   |

### Main `application.yml` File

```yaml
spring:
  application:
    name: todoapi
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway manages the schema.
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true

logging:
  level:
    root: INFO
    br.com.todoapi: DEBUG
```

---

## Running

### Local development

```bash
# Option 1: Use Maven Wrapper (recommended – no need to have Maven installed)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Option 2: With Maven installed globally
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Option 3: Build and run the JAR
./mvnw clean package -DskipTests
java -jar target/todoapi-1.0.0.jar --spring.profiles.active=dev
```

The API will be available at: **http://localhost:8080**

---

## Tests

### Test Coverage: ~75%, focusing on service, security, and integration tests.

### Running all tests

```bash
./mvnw test
```

### Specific tests

```bash
# Unit tests only
./mvnw test -Dtest="*ServiceTest"

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

### Coverage Report (JaCoCo)

```bash
./mvnw test jacoco:report
# Report at: target/site/jacoco/index.html
```

After running `./mvnw test jacoco:report`, the HTML report will be available at:

```
target/site/jacoco/index.html
```

Open this file in your browser to see detailed coverage by class and method.

---

## API Endpoints

### HTTP Responses

| Code  | Description                              |
|-------|------------------------------------------|
| `200` | Request executed successfully            |
| `201` | Resource created successfully            |
| `204` | Success with no response content         |
| `400` | Validation error or invalid data         |
| `401` | Unauthorized (missing or invalid token)  |
| `403` | Forbidden (not allowed)                  |
| `404` | Resource not found                       |
| `409` | Conflict (e.g., username already exists) |
| `500` | Internal server error                    |

### Public Endpoints (Auth)

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | `/api/auth/register` | Register user       |
| POST   | `/api/auth/login`    | Login (returns JWT) |

---

### **POST** `/api/auth/register`

Register a new user.

**Request Body:**

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "username": "newuser",
  "email": "newuser@example.com",
  "createdAt": "2025-12-23T10:00:00"
}
```

**Validations:**

- **Username:** 3–20 characters, alphanumeric
- **Email:** valid format, unique in the system
- **Password:** minimum 6 characters

---

### **POST** `/api/auth/login`

Log in and obtain a JWT token.

**Request Body:**

```json
{
  "username": "newuser",
  "password": "password123"
}
```

**Response:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

The JWT token will be used to perform the remaining **task** requests.

---

### Protected Endpoints (Tasks)

| Method | Endpoint                    | Description            |
|--------|-----------------------------|------------------------|
| POST   | `/api/tasks`                | Create task            |
| GET    | `/api/tasks`                | List tasks (paginated) |
| GET    | `/api/tasks?status=PENDING` | Filter by status       |
| GET    | `/api/tasks/{id}`           | Get by ID              |
| PUT    | `/api/tasks/{id}`           | Update task            |
| PATCH  | `/api/tasks/{id}/status`    | Update status          |
| DELETE | `/api/tasks/{id}`           | Delete task            |

**All endpoints below require JWT authentication!**

Add the header to all requests:

```
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

---

#### **POST** `/api/tasks`

Create a new task.

**Request Body:**

```json
{
  "title": "Implement feature X",
  "description": "Detailed task description",
  "status": "PENDING",
  "priority": "HIGH"
}
```

**Response:** `201 Created`

```json
{
  "id": 1,
  "title": "Implement feature X",
  "description": "Detailed task description",
  "status": "PENDING",
  "priority": "HIGH",
  "createdAt": "2025-12-23T10:00:00",
  "updatedAt": "2025-12-23T10:00:00"
}
```

**Valid statuses:** `PENDING`, `IN_PROGRESS`, `COMPLETED`  
**Valid priorities:** `LOW`, `MEDIUM`, `HIGH`

---

#### **GET** `/api/tasks`

List tasks with pagination and filters.

**Query Parameters:**

| Parameter  | Type   | Default        | Description                                        |
|------------|--------|----------------|----------------------------------------------------|
| `page`     | int    | 0              | Page number (starts at 0)                          |
| `size`     | int    | 10             | Items per page                                     |
| `sort`     | string | createdAt,desc | Field and direction (e.g., title,asc)              |
| `status`   | enum   | -              | Filter by status (PENDING, IN_PROGRESS, COMPLETED) |
| `priority` | enum   | -              | Filter by priority (LOW, MEDIUM, HIGH)             |
| `title`    | string | -              | Search by title (case insensitive)                 |

**Example:**

```
GET /api/tasks?page=0&size=5&sort=priority,desc&status=PENDING
```

**Response:** `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "title": "Task 1",
      "description": "Description",
      "status": "PENDING",
      "priority": "HIGH",
      "createdAt": "2025-12-23T10:00:00",
      "updatedAt": "2025-12-23T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 15,
  "totalPages": 3,
  "last": false,
  "first": true
}
```

---

#### **GET** `/api/tasks/{id}`

Get a task by ID.

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "My Task",
  "description": "Description",
  "status": "PENDING",
  "priority": "HIGH",
  "createdAt": "2025-12-23T10:00:00",
  "updatedAt": "2025-12-23T10:00:00"
}
```

**Possible errors:**

- `404 Not Found` - Task does not exist or belongs to another user

---

#### **PUT** `/api/tasks/{id}`

Update a task completely (all fields).

**Request Body:**

```json
{
  "title": "Updated title",
  "description": "New description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM"
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "Updated title",
  "description": "New description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "createdAt": "2025-12-23T10:00:00",
  "updatedAt": "2025-12-23T12:00:00"
}
```

---

#### **PATCH** `/api/tasks/{id}/status`

Update only the task status.

**Request Body:**

```json
{
  "status": "COMPLETED"
}
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "title": "My task",
  "description": "Description",
  "status": "COMPLETED",
  "priority": "HIGH",
  "createdAt": "2025-12-23T10:00:00",
  "updatedAt": "2025-12-23T14:00:00"
}
```

---

#### **DELETE** `/api/tasks/{id}`

Delete a task.

**Response:** `204 No Content`

**Possible errors:**

- `404 Not Found` - Task does not exist or belongs to another user

---

### Examples with curl

**1. Register user:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123"
  }'
```

**2. Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123"
  }'
```

**3. Create Task (with JWT):**

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "My task",
    "description": "Description",
    "status": "PENDING",
    "priority": "HIGH"
  }'
```

**4. List tasks with filter (status=PENDING):**

```bash
curl -X GET "http://localhost:8080/api/tasks?status=PENDING&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**5. Update status:**

```bash
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"status": "COMPLETED"}'
```

---

## Swagger

Interactive documentation available via Swagger UI at:

**Local Development:** http://localhost:8080/swagger-ui.html

### How to use Swagger

1. **Access Swagger UI** in your browser
2. **Log in** using the endpoint `/api/auth/login` or `/api/auth/register`
3. **Copy the returned JWT Token**
4. **Click "Authorize"** (green lock button at the top)
5. **Paste the token** in the format: `Bearer YOUR_JWT_TOKEN_HERE`
6. **Click "Authorize"**
7. **Test all endpoints** directly through Swagger

### Swagger Features

- Complete documentation of all endpoints
- Interactive request testing
- Request/response schemas
- Documented validations and constraints
- Integrated JWT authentication
- Usage examples for each endpoint

---

## License
[MIT](LICENSE)

---

## Author

**Carlos Paulon**  
Backend Developer | Java & Spring Boot

[![LinkedIn](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/carlospaulon/)
[![GitHub](https://img.shields.io/badge/GitHub-000000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/carlospaulon)
