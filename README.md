
# Foodya Backend API

RESTful API for Foodya - A food delivery platform built with Spring Boot 3.x, PostgreSQL, and JWT authentication.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Authentication](#-authentication)
- [Project Structure](#-project-structure)
- [Docker Support](#-docker-support)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- 🔐 **JWT Authentication & Authorization** with role-based access control (RBAC)
- 👥 **User Management** with 4 roles: CUSTOMER, MERCHANT, DELIVERY, ADMIN
- 🍽️ **Restaurant Management** (CRUD operations)
- 📋 **Menu Item Management** with categories and filters
- 🔍 **Advanced Search & Filtering** (by cuisine, rating, popularity)
- 🛡️ **Resource Ownership Validation** (merchants can only edit their own restaurants)
- 📊 **Admin Dashboard** for user and system management
- 📝 **API Documentation** with Swagger/OpenAPI 3.0
- 🐳 **Docker Support** for easy deployment
- 🔄 **Refresh Token** mechanism for seamless authentication

---

## 🛠️ Tech Stack

### Core Technologies
- **Java 21** - Programming language
- **Spring Boot 3.3.8** - Application framework
- **Spring Security 6** - Authentication & Authorization
- **Spring Data JPA** - Data access layer
- **Hibernate 6.5** - ORM framework

### Database
- **PostgreSQL 16** - Primary database
- **Supabase** - Managed PostgreSQL hosting (optional)
- **UUID** - Primary key strategy

### Security & Validation
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Jakarta Validation** - Request validation

### Documentation & Tools
- **Swagger/OpenAPI 3.0** - API documentation
- **Lombok** - Reduce boilerplate code
- **Maven** - Build tool

### DevOps
- **Docker & Docker Compose** - Containerization
- **Spring Boot Actuator** - Health checks & monitoring

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **PostgreSQL 16** (or use Supabase cloud database)
- **Git** ([Download](https://git-scm.com/downloads))
- **Docker** (optional, for containerized deployment)


```bash

🚀 Installation
1. Clone repository
git clone https://github.com/yourusername/foodya.git
cd foodya/foodya-backend

2. Database configuration
Option A: Supabase
spring.datasource.url=jdbc:postgresql://YOUR_HOST:6543/postgres?sslmode=require
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

Option B: Local PostgreSQL
CREATE DATABASE foodya_db;

spring.datasource.url=jdbc:postgresql://localhost:5432/foodya_db
spring.datasource.username=postgres
spring.datasource.password=your_password

3. Build
mvn clean install

⚙️ Configuration
Environment Variables
DB_URL=jdbc:postgresql://localhost:5432/foodya_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_256_bit_secret_key
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

application.properties
server.port=8080

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-time=${JWT_EXPIRATION}
app.jwt.refresh-expiration-time=${JWT_REFRESH_EXPIRATION}

springdoc.swagger-ui.path=/swagger-ui.html

🏃 Running the Application
Maven
mvn spring-boot:run

JAR
java -jar target/foodya-backend-0.0.1-SNAPSHOT.jar

Docker
docker-compose up -d

📚 API Documentation

Swagger UI:

http://localhost:8080/swagger-ui.html


Base URL:

http://localhost:8080/api/v1

🔐 Authentication
Register
POST /api/v1/auth/register

{
  "username": "john",
  "email": "john@example.com",
  "password": "Secure@123",
  "fullName": "John Doe",
  "phoneNumber": "+84987654321",
  "role": "MERCHANT"
}

Login
POST /api/v1/auth/login

Authorization Header
Authorization: Bearer <ACCESS_TOKEN>

📁 Project Structure
foodya-backend/
├── admin/
├── auth/
├── config/
├── middleware/
├── restaurant/
├── user/
└── FoodyaBackendApplication.java

🐳 Docker Support
docker build -t foodya-backend .
docker run -p 8080:8080 foodya-backend

🧪 Testing
mvn test


Coverage:

target/site/jacoco/index.html

🐛 Troubleshooting
Port already in use
lsof -ti:8080 | xargs kill -9

JWT Invalid

Secret ≥ 256 bits

Token not expired

Correct Bearer format

🤝 Contributing
git checkout -b feature/your-feature
git commit -m "feat: add feature"
git push origin feature/your-feature


Follow Conventional Commits.

📄 License

MIT License

👨‍💻 Author

Your Name
https://github.com/yourusername

🗺️ Roadmap

 Order system

 Payment integration

 Delivery tracking

 Redis caching

 Rate limiting

 Cloud deployment
