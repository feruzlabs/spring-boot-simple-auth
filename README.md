# Spring Boot Simple Auth

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-red.svg)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://docs.docker.com/compose/)

A modern, secure Spring Boot authentication system using JWT, refresh tokens, and role-based access control.

## Features

- JWT-based authentication (HS512)
- Refresh token flow
- BCrypt password hashing
- Role-based authorization: USER, ADMIN
- Stateless security with custom JWT filter
- CORS enabled
- Request validation (Jakarta Validation)

## Implemented Flows

- Register -> Login -> Access with Bearer token
- Refresh access token via refresh token
- Logout (current device) and logout from all devices
- Forgot password -> Reset password via token
- Change password (with current password)
- Get current user ("/api/auth/me")

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optional for DB)

### Start PostgreSQL with Docker
```bash
docker-compose up -d
```

### Run the Application
```bash
mvn spring-boot:run
# or
mvn clean package && java -jar target/spring-boot-auth-0.0.1-SNAPSHOT.jar
```

### Open
- App: http://localhost:8020
- Swagger UI: http://localhost:8020/swagger-ui.html

## Configuration

File: src/main/resources/application.properties
```properties
server.port=8020
spring.application.name=spring-boot-auth

spring.datasource.url=jdbc:postgresql://localhost:5440/db
spring.datasource.username=user
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=myVeryLongSecretKeyThatIsAtLeast64CharactersLongForHS512AlgorithmSecurity123456789
jwt.expiration=86400000
```

File: docker-compose.yml
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    container_name: my_postgres_db2
    restart: always
    environment:
      POSTGRES_DB: db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5440:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
volumes:
  postgres_data:
```

## API Reference

Base URL: http://localhost:8020

### Auth (prefix: /api/auth)

- POST /register — Register new user
  - Body:
  ```json
  { "username": "john", "email": "john@example.com", "password": "secret123" }
  ```
  - 200 OK -> { message, success }

- POST /login — Login with username/password
  - Body:
  ```json
  { "username": "john", "password": "secret123" }
  ```
  - 200 OK -> JwtResponse { token, type, username, role }

- POST /token/refresh — Get new access token using refresh token
  - Body:
  ```json
  { "refreshToken": "<refresh-token>" }
  ```
  - 200 OK -> JwtResponse

- POST /logout — Logout from current device (Auth required)
  - 200 OK -> { message, success }

- POST /logout/all — Logout from all devices (Auth required)
  - 200 OK -> { message, success }

- GET /username/check?username=john — Check username availability
  - 200 OK -> { message, success }

- POST /password/forget — Initiate password reset (email based)
  - Body:
  ```json
  { "email": "john@example.com" }
  ```
  - 200 OK -> Always { message, success }

- POST /password/reset — Reset password using token
  - Body:
  ```json
  { "token": "<reset-token>", "newPassword": "newSecret123" }
  ```
  - 200 OK/400 -> { message, success }

- POST /password/change — Change password (Auth required)
  - Body:
  ```json
  { "currentPassword": "secret123", "newPassword": "newSecret123" }
  ```
  - 200 OK/400 -> { message, success }

- GET /me — Get current user info (Auth required)
  - 200 OK -> user info object

### Test (prefix: /api/test)
- GET /hello — Protected hello (Auth required)
- GET /profile — Auth info snapshot (Auth required)
- GET /admin/page — Admin-only (Role ADMIN)
- GET /user/page — User-only (Role USER)
- POST /echo — Echo body
- GET /secure — Secure endpoint (Auth required)
- GET /debug — Debug authentication data

## Security

- SecurityConfig:
  - Disables CSRF for stateless JWT
  - Permits: /api/auth/**, /swagger-ui/**, /v3/api-docs/**, /
  - Protects: all other endpoints
  - Adds JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
- JwtAuthenticationFilter:
  - Reads Authorization: Bearer <token>
  - Validates token, sets SecurityContext with authorities: ROLE_USER / ROLE_ADMIN
- Passwords stored with BCrypt

## Domain Model

- User entity: id, username (unique), password, email, role
- Role enum: USER, ADMIN

## Usage Examples (cURL)

```bash
# Register
curl -X POST http://localhost:8020/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'

# Login
curl -X POST http://localhost:8020/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}'
# Save token from response

# Access protected
curl -H "Authorization: Bearer $TOKEN" http://localhost:8020/api/test/hello

# Refresh token
curl -X POST http://localhost:8020/api/auth/token/refresh \
  -H "Content-Type: application/json" -d '{"refreshToken":"<REFRESH>"}'

# Change password
curl -X POST http://localhost:8020/api/auth/password/change \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"currentPassword":"secret123","newPassword":"newSecret123"}'
```

## Project Structure

```
src/main/java/dev/feruzlabs/springbootauth/
├── configs/
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controllers/
│   ├── AuthController.java
│   ├── MainController.java
│   └── TestController.java
├── dto/
│   ├── request/ (LoginRequest, RegisterRequest, Forgot/Reset/ChangePasswordRequest, RefreshTokenRequest)
│   ├── response/ (JwtResponse, MessageResponse)
│   └── CurrentUserDTO
├── entities/ (User)
├── enums/ (Role)
├── repositories/ (UserRepository)
├── securities/ (JwtAuthenticationFilter, JwtUtil)
├── services/ (AuthService, RefreshTokenService)
└── SpringBootAuthApplication.java
```

## Tech Stack
- Spring Boot, Spring Security, Spring Data JPA
- PostgreSQL, Docker Compose
- JJWT (io.jsonwebtoken), Lombok, Springdoc OpenAPI

## Contributing
PRs are welcome: Branch -> PR -> Review -> Merge.

## License
MIT (add LICENSE file if needed)
