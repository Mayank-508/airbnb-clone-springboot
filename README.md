# AirBnb Clone - Backend API

## Overview
A comprehensive backend API for an AirBnb-like application, built using **Java 21** and **Spring Boot**. The API provides robust functionalities for hotel and room management, booking flows, inventory tracking, and user authentication using JSON Web Tokens (JWT). This project demonstrates a production-ready application architecture using layered design patterns and Spring Data JPA.

## Key Features
- **User Authentication:** Secure signup and login flows utilizing Spring Security and JWT.
- **Hotel & Room Management:** Manage and browse hotel details, contact information, and associated rooms.
- **Booking System:** Complete booking lifecycle, including guest details and payment tracking.
- **Inventory Management:** Efficient tracking of available rooms to prevent double bookings.
- **DTO Mapping:** Clean separation between database entities and API payload structures using ModelMapper.
- **Exception Handling:** Centralized global exception handling with `@RestControllerAdvice`.

## Tech Stack
- **Java 21**: Modern language features.
- **Spring Boot 4.0.3**: Includes Spring Web, Spring Data JPA, Spring Security, and Validation.
- **PostgreSQL**: Relational database for persistent storage.
- **Lombok**: Boilerplate reduction for data classes.
- **jjwt (JSON Web Token)**: Managing secure API requests.
- **ModelMapper**: Seamlessly mapping Entities to and from Data Transfer Objects.
- **Maven**: Build and dependency management.

## Project Architecture
The project follows a standard multi-tiered architecture:
- `controller/`: REST endpoints for resources (e.g., `HotelController`, `RoomController`, etc.).
- `service/`: Core business logic connecting controllers and repositories.
- `repository/`: Spring Data JPA interfaces for database operations.
- `entity/`: Database abstractions mapping to tables (`Hotel`, `Room`, `Booking`, `User`, etc.).
- `dto/`: Data Transfer Objects defining the structure of web requests and responses.
- `config/` & `advice/`: Security configuration, JWT filters, and global error handling.

## Getting Started

### Prerequisites
- JDK 21 installed and configured.
- PostgreSQL running locally (default port `5432`).
- Maven (or use the provided `mvnw` wrapper).

### Database Setup
1. Create a PostgreSQL database called `airBnb`.
2. Configure the connection properties in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/airBnb
   spring.datasource.username=postgres
   spring.datasource.password=12345
   ```

### Running the Application
You can run the application directly using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
The server will start on port `8080`. All API endpoints are globally prefixed with `/api/v1`.

## Development & Testing
The project includes dependencies for unit and integration testing:
- `spring-boot-starter-test`
- `spring-boot-starter-webmvc-test`
- `spring-boot-starter-data-jpa-test`

You can run test suites using Maven:
```bash
./mvnw test
```