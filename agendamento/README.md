# 📅 Pet Appointment Microservice

This microservice is responsible for managing pet care appointments, both automatic and manual, based on pet data received from the Pet Registration microservice.

---

## 🚀 Features

- Automatically creates appointments for new or updated pets based on clinical rules:
  - Initial vaccination for puppies
  - Annual vaccination and check-up for adults
- Allows manual creation of appointments by requesting pet data asynchronously
- Publishes appointment events (`appointment.created`, `appointment.updated`, `appointment.cancelled`) to other services like Notifications
- List, retrieve, confirm, and delete appointments via REST API

---

## 📦 REST API Endpoints

| Method | Endpoint                      | Description                                   |
|--------|-------------------------------|-----------------------------------------------|
| POST   | `/appointments`               | Request manual appointment creation           |
| GET    | `/appointments`               | List all appointments                         |
| GET    | `/appointments/{id}`          | Get appointment details by ID                 |
| DELETE | `/appointments/{id}`          | Delete appointment by ID                      |
| PATCH  | `/appointments/{id}/confirm`  | Confirm an appointment manually               |
| GET    | `/appointments/types`         | List appointments filtered by type and date   |

---

## 🧬 Architecture Overview

- Language: Java 21
- Framework: Spring Boot
- Messaging: RabbitMQ
- API Docs: OpenAPI 3 (Swagger)
- Build tool: Maven

---

## 📨 Messaging Integration (RabbitMQ)

### Events Published

| Event Type            | Routing Key               | Triggered When                        |
|------------------------|----------------------------|----------------------------------------|
| `appointment.created`  | `appointment.created`      | On appointment creation               |
| `appointment.updated`  | `appointment.updated`      | On appointment update                 |
| `appointment.cancelled`| `appointment.cancelled`    | On appointment cancellation           |
| `pet_info_request`     | `pet.info.request`          | On manual appointment pet info request|

### Events Consumed

| Event Type               | Queue                         | Description                                             |
|---------------------------|-------------------------------|---------------------------------------------------------|
| `pet.created`             | `pet.created`                 | Automatically create initial appointments               |
| `pet.updated`             | `pet.updated`                 | Update appointments based on new pet data               |
| `pet_info_response`       | `pet.responses`               | Receives pet data for manual appointment                |
| `appointment.is.confirmed`| `appointment.is.confirmed.by.mail` | Update appointment as confirmed via email link    |
| `appointment.is.cancelled`| `appointment.is.cancelled.by.mail` | Cancel appointment via email link                    |

> ⚠️ This service declares all queues it consumes and the necessary routing keys for RabbitMQ integration.

---

## 📁 DTO Responsibilities

| DTO                      | Purpose                             |
|---------------------------|-------------------------------------|
| `AppointmentRequestDTO`   | Request for creating appointments  |
| `AppointmentResponseDTO`  | Response with appointment details  |
| `PetInfoRequestEvent`     | Event for requesting pet info      |
| `PetEventDTO`             | Received pet data (from Cadastro)  |

---

## ✅ Good Practices Applied

- Centralized validation and business rules
- Asynchronous communication with retries and error handling
- Swagger documentation for API endpoints
- Clean service/controller separation

---

## 📂 Project Structure

```
agendamento/
├── src/
│   ├── main/
│   │   ├── java/com/ms/agendamento/
│   │   │   ├── config/               # Configurations (Swagger, RabbitMQ, etc)
│   │   │   ├── controller/           # REST controllers
│   │   │   ├── dto/                  # Data Transfer Objects (DTOs)
│   │   │   ├── enums/                # Enum types (AppointmentType)
│   │   │   ├── exception/            # Custom exceptions
│   │   │   ├── messaging/            # Consumers and Producers (RabbitMQ)
│   │   │   ├── model/                # JPA entities
│   │   │   ├── repository/           # Spring Data JPA repositories
│   │   │   └── service/              # Business logic and rules
│   └── resources/
│       ├── application.properties    # Configuration properties
└── pom.xml                          # Maven configuration
```

---

## 🛠️ Future Improvements

- Add unit and integration tests
- Add pagination and filtering improvements
- Allow rescheduling of confirmed appointments
- Add email reminders before appointments
