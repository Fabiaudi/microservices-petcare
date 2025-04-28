# 🐾 Pet Registration Microservice

This microservice is responsible for registering pets, managing their information, and integrating with external APIs and other microservices in the pet care ecosystem.

---

## 🚀 Features

- Register pets with detailed information: name, species, breed, birth date, color, weight, description, image, temperament, etc.
- Associate pets with one or more owners (tutors).
- Integrate with external APIs (TheDogAPI, TheCatAPI) to enrich pet data with images and temperament.
- Publish events (`pet_created`, `pet_updated`) via RabbitMQ to notify other services.
- Respond to asynchronous pet info requests (`pet_info_request` → `pet_info_response`) from the Appointment microservice.
- Automatically enrich pet data with breed images and descriptions based on the species and breed provided.
- Manage pet owners with CPF-based identification.

---

## 📦 REST API Endpoints

| Method | Endpoint       | Description                       |
|--------|----------------|-----------------------------------|
| POST   | `/pets`        | Register a new pet                |
| PUT    | `/pets/{id}`   | Update an existing pet            |
| DELETE | `/pets/{id}`   | Delete a pet                      |
| GET    | `/pets`        | List all pets                     |
| GET    | `/pets/search` | Search pets by species or breed   |
| GET    | `/pets/{id}`   | Get pet details by ID             |
| POST   | `/owners`      | Register a new owner              |
| PATCH  | `/owners/{oldCpf}/change-cpf` | Change owner's CPF |

---

## 🧬 Architecture Overview

- Language: Java 21
- Framework: Spring Boot
- Messaging: RabbitMQ
- API Docs: OpenAPI 3 (Swagger)
- Build tool: Maven
- ORM: Spring Data JPA (MySQL)

---

## 📨 Messaging Integration (RabbitMQ)

### Events Published

| Event Type      | Routing Key     | Triggered When         |
|-----------------|------------------|-------------------------|
| `pet_created`   | `pet.created`    | On pet registration     |
| `pet_updated`   | `pet.updated`    | On pet update           |
| `pet_info_response` | `pet.responses` | Responding with pet data |

### Events Consumed

| Event Type        | Queue            | Description                                 |
|-------------------|------------------|---------------------------------------------|
| `pet_info_request`| `pet.requests`   | Receives a pet ID and returns full data     |

> ⚠️ Queue `pet.requests` is declared **only in this service**, since it listens to it.  
> ⚠️ Queue `pet.responses` is declared by the Appointment service.

---

## 📁 DTO Responsibilities

| DTO             | Purpose                    |
|-----------------|-----------------------------|
| `PetDTO`         | Used for REST input/output  |
| `PetEventDTO`    | Used for RabbitMQ messages  |
| `OwnerDTO`       | Embedded in pet data        |

---

## ✅ Good Practices Applied

- Separation of DTOs for REST vs Events
- Custom exceptions with proper error handling
- Swagger documentation with detailed annotations
- Clean code with proper service/controller layering
- Environment variable configuration for external API keys
- Docker-ready structure for containerized deployment

---

## 📂 Project Structure

```
cadastro/
├── src/
│   ├── main/
│   │   ├── java/com/ms/cadastro/
│   │   │   ├── config/               # Configurations (Swagger, RabbitMQ, etc)
│   │   │   ├── controller/           # REST controllers
│   │   │   ├── dto/                  # Data Transfer Objects (DTOs)
│   │   │   ├── enums/                # Enum types (Species, Temperament)
│   │   │   ├── exception/            # Custom exceptions
│   │   │   ├── external/             # Integration with external APIs
│   │   │   ├── mapper/               # MapStruct interfaces for entity/DTO conversion
│   │   │   ├── model/                # JPA entities
│   │   │   ├── producer/             # Event publishers (RabbitMQ)
│   │   │   ├── repository/           # Spring Data JPA repositories
│   │   │   └── service/              # Business logic
│   └── resources/
│       ├── application.properties    # Configuration properties
│       └── static/                   # Static resources (if any)
└── pom.xml                          # Maven configuration
```

---

## 🔐 Environment Variables Required

| Variable     | Purpose                           |
|--------------|-----------------------------------|
| `X-API-KEY`  | API Key to access TheDogAPI/TheCatAPI |

**Example configuration (Docker Compose):**
```yaml
environment:
  - X-API-KEY=your-api-key-here
```

---

## 🛠️ Future Improvements

- Add unit and integration tests
- Add pagination to `/pets` endpoint
- Add filters for color, temperament, weight range, etc.
- Centralize error responses in a unified format (ProblemDetails)
- Implement validation annotations directly on DTOs

---

# 🔥 Summary

This microservice is ready for scalable production use and follows clean code principles, supporting future growth and integration with more services.
