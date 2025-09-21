# 🔐 Spring Boot Simple Auth

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-red.svg)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://docs.docker.com/compose/)

A modern, secure, and production-ready Spring Boot authentication system with JWT (JSON Web Token) implementation. This project provides a complete authentication and authorization solution with role-based access control.

## ✨ Features

### 🔐 Authentication & Security
- **JWT-based Authentication** - Stateless token-based authentication
- **BCrypt Password Encryption** - Secure password hashing
- **Role-based Authorization** - USER and ADMIN roles
- **CORS Support** - Cross-origin resource sharing enabled
- **Input Validation** - Comprehensive request validation

### 🏗️ Architecture
- **Spring Boot 3.5.6** - Latest Spring Boot framework
- **Spring Security** - Enterprise-grade security
- **Spring Data JPA** - Database abstraction layer
- **PostgreSQL** - Robust relational database
- **Docker Compose** - Containerized development environment

### 📚 API Documentation
- **Swagger/OpenAPI** - Interactive API documentation
- **RESTful APIs** - Clean and intuitive endpoints
- **Comprehensive Testing** - Built-in test endpoints

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL (if running without Docker)

### 1. Clone the Repository
```ssh
git clone <repository-url>
cd spring-boot-auth
```

### 2. Start Database with Docker
```ssh
docker-compose up -d
```

### 3. Run the Application
```ssh
# Using Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/spring-boot-auth-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application
- **Application**: http://localhost:8020
- **API Documentation**: http://localhost:8020/swagger-ui.html
- **Database**: localhost:5440

## 📋 API Endpoints

### 🔓 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | User registration | ❌ |
| POST | /api/auth/login | User login | ❌ |
| GET | /api/auth/check-username | Check username availability | ❌ |

### 🔒 Protected Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | /api/test/hello | Protected hello message | ✅ | Any |
| GET | /api/test/profile | User profile information | ✅ | Any |
| GET | /api/test/admin/page | Admin-only page | ✅ | ADMIN |
| GET | /api/test/user/page | User-only page | ✅ | USER |
| GET | /api/test/secure | Secure endpoint | ✅ | Any |
| GET | /api/test/debug | Debug authentication info | ✅ | Any |

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8020
spring.application.name=spring-boot-auth

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5440/db
spring.datasource.username=user
spring.datasource.password=password

# JWT Configuration
jwt.secret=****
jwt.expiration=86400000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Docker Configuration
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    container_name: my_postgres_db2
    environment:
      POSTGRES_DB: db
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5440:5432"
```

## 📖 Usage Examples

### 1. User Registration
```ssh
curl -X POST http://localhost:8020/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securepassword123"
  }'
```

### 2. User Login
```ssh
curl -X POST http://localhost:8020/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securepassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "role": "USER"
}
```

### 3. Access Protected Endpoint
```ssh
curl -X GET http://localhost:8020/api/test/hello \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/dev/feruzlabs/springbootauth/
│   │   ├── configs/           # Configuration classes
│   │   │   ├── SecurityConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controllers/       # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── MainController.java
│   │   │   └── TestController.java
│   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── request/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── RegisterRequest.java
│   │   │   └── response/
│   │   │       ├── JwtResponse.java
│   │   │       └── MessageResponse.java
│   │   ├── entities/         # JPA Entities
│   │   │   └── User.java
│   │   ├── enums/           # Enumerations
│   │   │   └── Role.java
│   │   ├── repositories/     # Data Access Layer
│   │   │   └── UserRepository.java
│   │   ├── securities/      # Security Components
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── JwtUtil.java
│   │   ├── services/        # Business Logic
│   │   │   └── AuthService.java
│   │   └── SpringBootAuthApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/dev/feruzlabs/springbootauth/
        └── SpringBootAuthApplicationTests.java
```

## 🔒 Security Features

### JWT Implementation
- **Stateless Authentication** - No server-side session storage
- **Token Expiration** - 24-hour token lifetime
- **HMAC SHA-512** - Strong cryptographic signing
- **Bearer Token** - Standard Authorization header

### Password Security
- **BCrypt Hashing** - Industry-standard password hashing
- **Salt Generation** - Automatic salt generation for each password
- **Minimum Length** - 6 character minimum password requirement

### Role-Based Access Control
- **USER Role** - Standard user permissions
- **ADMIN Role** - Administrative privileges
- **Method-Level Security** - @PreAuthorize annotations

## 🧪 Testing

### Manual Testing with Swagger
1. Navigate to http://localhost:8020/swagger-ui.html
2. Use the interactive API documentation
3. Test authentication endpoints
4. Use the "Authorize" button to set JWT tokens

### API Testing with cURL
`ash
# Test registration
```ssh
curl -X POST http://localhost:8020/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
 ```


# Test login
```ssh
curl -X POST http://localhost:8020/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

# Test protected endpoint
```ssh
curl -X GET http://localhost:8020/api/test/hello \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
`
```

## 🐳 Docker Support

### Using Docker Compose
```ssh
# Start database
docker-compose up -d

# Stop database
docker-compose down

# View logs
docker-compose logs -f
```

### Database Connection
- **Host**: localhost
- **Port**: 5440
- **Database**: db
- **Username**: user
- **Password**: password

## 🔧 Development

### Running in Development Mode
```ssh
# Enable development tools
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Hot reload with devtools
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Database Management
```bash
# Connect to PostgreSQL
psql -h localhost -p 5440 -U user -d db

# View tables
\dt

# View users
SELECT * FROM users;
```

## 📝 Dependencies

### Core Dependencies
- **Spring Boot Starter Web** - Web application framework
- **Spring Boot Starter Security** - Security framework
- **Spring Boot Starter Data JPA** - Database abstraction
- **Spring Boot Starter Validation** - Input validation

### Security Dependencies
- **JJWT API** - JWT token handling
- **JJWT Implementation** - JWT implementation
- **JJWT Jackson** - JSON processing

### Development Dependencies
- **Spring Boot DevTools** - Development utilities
- **Lombok** - Code generation
- **SpringDoc OpenAPI** - API documentation
- **PostgreSQL Driver** - Database connectivity

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (git checkout -b feature/amazing-feature)
3. Commit your changes (git commit -m 'Add some amazing feature')
4. Push to the branch (git push origin feature/amazing-feature)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Feruz Labs**
- GitHub: [@feruzlabs](https://github.com/feruzlabs)

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Spring Security team for robust security features
- JWT.io for token standards
- PostgreSQL team for the excellent database

---

<div align="center">
  <p>Made with ❤️ by Feruz Labs</p>
  <p>⭐ Star this repository if you found it helpful!</p>
</div>
