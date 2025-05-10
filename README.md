# 🎬 Movie Review & Rating API - Java Spring Boot Project

## 📌 Overview

This project presents a comprehensive yet straightforward **Movie Review & Rating API**, integrating data from external systems.  
It serves as a clean reference for implementing similar capabilities in future backend applications.

## ✅ Features

### 🧩 Architecture & Core
- RESTful API with centralized exception handling
- Database integration:
  - PostgreSQL (relational)
  - MongoDB (document-oriented)
- Redis:
  - Caching
  - Distributed locks
- WebSocket support
- Conditional beans & asynchronous tasks
- Aspect-Oriented Programming (AOP)
- Prometheus metrics export
- Swagger UI for API documentation

### 🔐 Security
- Spring Security with JWT authentication
- Custom user and role management
- Custom filters for authentication/authorization

### 📦 Integration & Messaging
- AWS service integrations
- Messaging systems:
  - Amazon SQS
  - ActiveMQ (topics)

### 🧪 Testing & Quality
- JUnit for unit testing
- `@SpringBootTest` for integration testing
- TestContainers for infrastructure-level tests
- **100% code coverage** with JaCoCo

### 🛠️ DevOps & CI/CD
- Docker containerization
- Jenkins pipeline for build & deployment

### 🧹 Code Quality
- Lombok for cleaner code
- MapStruct for entity mapping

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/marlgrgr/PlaygroundMVC.git
cd PlaygroundMVC
```

### 2. Set up your environment

Ensure the following tools/services are available:
- Java 21
- Docker (for DBs and message brokers)
- PostgreSQL, MongoDB, Redis, ActiveMQ *(or use Docker Compose)*
- AWS credentials (for SQS usage)

> 🔧 To disable SQS, set the property:
>
> ```properties
> spring.cloud.aws.sqs.enabled=false
> ```

### 3. Run locally

Ensure `application.properties` is properly configured, including Redisson settings. Then, run the project with:

```bash
./mvnw spring-boot:run
```

### 4. Access Swagger UI

Open your browser and go to:

```text
http://localhost:8081/swagger-ui.html
```

### 5. Test with Postman

Use the provided Postman collection located at:

```text
src/main/resources/Playground.postman_collection.json
```

### 6. Run tests

#### Unit tests:

```bash
mvn clean test
```

#### Integration tests only:

```bash
mvn clean verify -DskipUnitTest=false -DskipIntegrationTest=false
```

#### Both unit & integration tests:

```bash
mvn clean verify -DskipIntegrationTest=false
```

> ✅ Test coverage results will be available at:  
>
> `target/site/jacoco/index.html`

### 7. Build and package

To run tests and generate a `.jar` in one step:

```bash
mvn clean install -DskipIntegrationTest=false
```
