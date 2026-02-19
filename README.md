# Customer Management Service

A RESTful API service for managing customer information, built with Java and Quarkus framework.

## Overview
Diagram
![Architecture](https://mermaid.ai/view/6dd69635-2553-4451-9d46-7dbac3c4b8ed)

This service provides a comprehensive solution for customer data management with the following features:
- Complete CRUD operations for customer management
- Country-based customer filtering
- Automatic demonym lookup using external REST Countries API
- Input validation and data integrity
- Comprehensive API documentation with Swagger UI
- Unit tests for business logic validation

## Technology Stack

- **Framework**: Quarkus 3.8.1
- **Language**: Java 17
- **Database**: H2 (In-memory)
- **ORM**: Hibernate ORM with Panache
- **API Documentation**: SmallRye OpenAPI + Swagger UI
- **Testing**: JUnit 5 + Mockito + REST Assured
- **Build Tool**: Maven
- **External Integration**: REST Countries API

## Prerequisites

- Java 17 or higher
- Maven 3.8.0 or higher
- Git

## Quick Start


### 1. Clone the Repository
```bash
cd customer-management-service
```

### 2. Build and Run the Application
```bash
# Development mode (with live reload)
./mvnw.cmd quarkus:dev

# Production build
./mvnw.cmd clean package
java -jar target/customer-management-service-1.0.0-SNAPSHOT-runner.jar
```

### 3. Access the Application

- **Application URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/
- **OpenAPI Spec**: http://localhost:8080/openapi

## API Endpoints

### Customer Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/customers` | Create a new customer |
| GET | `/customers` | Get all customers |
| GET | `/customers/{id}` | Get customer by ID |
| GET | `/customers/country/{countryCode}` | Get customers by country |
| PUT | `/customers/{id}` | Update customer (limited fields) |
| DELETE | `/customers/{id}` | Delete customer |

### Request/Response Examples

#### Create Customer
```json
POST /customers
Content-Type: application/json

{
  "firstName": "John",
  "secondName": "",
  "firstLastName": "Doe",
  "secondLastName": "",
  "email": "john.doe@example.com",
  "address": "123 Main St",
  "phone": "+1234567890",
  "countryCode": "US"
}
```

#### Update Customer
```json
PUT /customers/1
Content-Type: application/json

{
  "email": "new.email@example.com",
  "address": "456 New St",
  "phone": "+9876543210",
  "countryCode": "CA"
}
```

#### Customer Response
```json
{
  "id": 1,
  "firstName": "John",
  "secondName": "",
  "firstLastName": "Doe",
  "secondLastName": "",
  "email": "john.doe@example.com",
  "address": "123 Main St",
  "phone": "+1234567890",
  "countryCode": "US",
  "demonym": "American",
  "createdAt": "2026-02-18T17:00:00",
  "updatedAt": "2026-02-18T17:00:00"
}
```

## Data Model

### Customer Entity

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto-generated | Unique identifier |
| firstName | String | First name (max 100 chars) |
| secondName | String | Second name (max 100 chars) |
| firstLastName | String | First last name (max 100 chars) |
| secondLastName | String | Second last name (max 100 chars) |
| email | String | Email address (unique, valid format) |
| address | String | Address (max 255 chars) |
| phone | String | Phone number (10-15 digits, optional +) |
| countryCode | String | ISO 3166-1 alpha-2 country code |
| demonym | String | Country demonym from external API |
| createdAt | LocalDateTime | Auto-generated | Creation timestamp |
| updatedAt | LocalDateTime | Auto-generated | Last update timestamp |

## Validation Rules

- **Email**: Must be valid email format and unique
- **Phone**: Must be 10-15 digits, optional + prefix
- **Country Code**: Must be valid ISO 3166-1 alpha-2 code (2 uppercase letters)
- **Required Fields**: firstName, firstLastName, email, address, phone, countryCode
- **Optional Fields**: secondName, secondLastName

## External Integration

The service integrates with [REST Countries API](https://restcountries.com) to automatically populate the demonym field based on the country code provided.

- **Endpoint**: https://restcountries.com/v3.1/alpha/{code}
- **Purpose**: Fetch country demonym information
- **Error Handling**: Service continues without demonym if external API fails

## Testing

### Running Tests
```bash
# Run all tests
./mvnw.cmd test

# Run specific test class
./mvnw.cmd test -Dtest=CustomerServiceTest
./mvnw.cmd test -Dtest=CustomerResourceTest
```

### Test Coverage
- **CustomerServiceTest**: Business logic validation
- **CustomerResourceTest**: REST API endpoint testing
- **Mocking**: External dependencies and repository layer

## Configuration

### Application Properties
Key configuration options in `src/main/resources/application.properties`:

```properties
# Server configuration
quarkus.http.port=8080

# Database configuration
quarkus.datasource.db-kind=h2
quarkus.datasource.username=sa
quarkus.datasource.password=
quarkus.datasource.jdbc.url=jdbc:h2:mem:default;DB_CLOSE_DELAY=-1

# Hibernate configuration
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=import.sql

# External API configuration
restcountries-api/mp-rest/url=https://restcountries.com/v3.1

# OpenAPI/Swagger configuration
quarkus.smallrye-openapi.path=/openapi
quarkus.swagger-ui.path=/swagger-ui
```

## Project Structure

```
customer-management-service/
├── .mvn/                          # Maven wrapper files
├── apache-maven-3.9.6/            # Maven distribution
├── docs/                           # Documentation
│   └── README.md                   # Additional docs
├── src/
│   ├── main/
│   │   ├── java/com/example/customer/
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   │   ├── CreateCustomerRequest.java
│   │   │   │   └── UpdateCustomerRequest.java
│   │   │   ├── exception/          # Exception handlers
│   │   │   │   ├── ApplicationExceptionMapper.java
│   │   │   │   ├── FieldValidationException.java
│   │   │   │   └── InvalidFieldException.java
│   │   │   ├── model/              # JPA entities
│   │   │   │   └── Customer.java
│   │   │   ├── repository/         # Data access layer
│   │   │   │   └── CustomerRepository.java
│   │   │   ├── resource/           # REST controllers
│   │   │   │   └── CustomerResource.java
│   │   │   └── service/            # Business logic
│   │   │       ├── CountryService.java
│   │   │       ├── CountryResponse.java
│   │   │       └── CustomerService.java
│   │   └── resources/
│   │       ├── application.properties  # Configuration
│   │       └── import.sql          # Sample data
│   └── test/
│       └── java/com/example/customer/
│           ├── resource/             # API tests
│           │   └── CustomerResourceTest.java
│           └── service/              # Service tests
│               └── CustomerServiceTest.java
├── target/                         # Build output
│   ├── classes/                  # Compiled classes
│   └── customer-management-service-*.jar
├── mvnw                           # Maven wrapper script (Unix)
├── mvnw.cmd                       # Maven wrapper script (Windows)
├── pom.xml                        # Maven configuration
└── README.md                       # This file
```

### Key Components

- **DTOs**: Request/response objects for API communication
- **Exception Handlers**: Global error handling and validation
- **Model**: JPA entity with database mapping
- **Repository**: Data access using Panache (Hibernate)
- **Resource**: REST API endpoints with validation
- **Service**: Business logic and external API integration
- **Tests**: Unit tests for service and integration tests for API

## Development

### Adding New Features
1. Create/update DTOs in `dto/` package
2. Implement business logic in `service/` package
3. Add REST endpoints in `resource/` package
4. Write unit tests for new functionality
5. Update API documentation

### Code Quality
- Follow Java naming conventions
- Use Bean Validation for input validation
- Implement proper error handling
- Write comprehensive unit tests
- Document API endpoints with Swagger annotations

## Deployment

### Local Development
```bash
./mvnw.cmd quarkus:dev
```

### Production Build
```bash
./mvnw.cmd clean package -DskipTests
java -jar target/customer-management-service-1.0.0-SNAPSHOT-runner.jar
```

### Docker Deployment
```bash
# Build native image
./mvnw.cmd package -Pnative

# Run with Docker
docker build -f src/main/docker/Dockerfile.native -t customer-service .
docker run -p 8080:8080 customer-service
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   - Change port in `application.properties`
   - Kill existing process: `taskkill /F /IM java.exe`

2. **External API Failures**
   - Check internet connectivity
   - Verify country codes are valid ISO 3166-1 alpha-2

3. **Database Issues**
   - Database is automatically recreated on startup
   - Check `import.sql` for sample data issues

### Logs
Application logs are displayed in the console when running in development mode. Check for error messages and warnings.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Update documentation
5. Submit a pull request

## License

This project is for demonstration purposes as part of a technical assessment.
