# 🐾 Pet Care Microservices Architecture

This project is a distributed system composed of microservices to manage pet registrations, veterinary appointments, and notification communications.

It simulates a real-world ecosystem where services are integrated asynchronously via RabbitMQ and discoverable via Eureka Server.

---

## 🎗️ Microservices Overview

| Microservice         | Description                                                                  |
|----------------------|------------------------------------------------------------------------------|
| **Eureka Server**     | Service Discovery for all microservices.                                     |
| **API Gateway**       | Entry point for all client requests (proxy and routing).                    |
| **Pet Registration**  | Manages pet and owner registration, integrates with external pet APIs.      |
| **Appointment Service** | Manages veterinary appointment scheduling (automatic/manual).            |
| **Notification Service** | Sends emails about pet registrations and appointment events.            |

---

## 📺 Architecture Diagram

```
Client → API Gateway → [Cadastro | Agendamento | Notificação]
                ➙
             Eureka Server (Service Registry)
RabbitMQ (Event-driven communication between services)
```

---

## 🚀 Technologies Used

- **Java 21**
- **Spring Boot 3**
- **Spring Cloud (Eureka, Config)**
- **Spring AMQP (RabbitMQ)**
- **RabbitMQ 3 (Management Plugin)**
- **MySQL 8.0**
- **Maven**
- **Swagger (OpenAPI 3)**
- **Docker & Docker Compose**

---

## 📨 Asynchronous Messaging (RabbitMQ)

| Event Type                 | Producer Microservice | Consumer Microservice(s)        |
|-----------------------------|------------------------|----------------------------------|
| `pet.created`               | Pet Registration        | Notification, Appointment       |
| `pet.updated`               | Pet Registration        | Appointment                     |
| `pet_info_request`          | Appointment             | Pet Registration (response)     |
| `pet_info_response`         | Pet Registration        | Appointment                     |
| `appointment.created`       | Appointment             | Notification                    |
| `appointment.updated`       | Appointment             | Notification                    |
| `appointment.confirmed`     | Notification             | Appointment                     |
| `appointment.cancelled`     | Notification             | Appointment                     |

---

## 🧹 Project Structure

```
Microservices/
├── api-gateway/           # Gateway service
├── cadastro/              # Pet registration service
├── agendamento/           # Appointment management service
├── notificacao/           # Notification service (emails)
└── eurekaServer/          # Eureka service discovery
    docker-compose.yml     # Docker orchestration
```

---

## 🐳 Running with Docker

1. Make sure Docker and Docker Compose are installed.
2. Build and start all services:

```bash
docker-compose up --build
```

- Services will be available at:
  - **Eureka**: http://localhost:8761
  - **API Gateway**: http://localhost:9191
  - **RabbitMQ Management**: http://localhost:15672 (guest/guest)

---

## ⚙️ Environment Variables

The Pet Registration Service requires an API Key to fetch pet breed images:
- `x-api-key` (in Docker Compose for cadastro-service)

---

## ✅ Good Practices Applied

- Clean code with separation of concerns.
- DTOs separated for REST and Messaging.
- Asynchronous communication for resilience.
- RabbitMQ exchanges, queues, and routing keys organized.
- Swagger documentation for all REST APIs.
- HTML email templates with action buttons and security tokens.

---

## 🛠️ Future Improvements

- Add resilience patterns (Retry, Circuit Breaker).
- Add unit and integration tests.
- Implement monitoring and distributed tracing.
- Improve security (OAuth2, API Gateway Filters).

---

# 📚 Conclusion

This project demonstrates a practical, modular, and scalable microservices architecture applied to a real-world domain. It can be extended easily to include more services or more complex event-driven behaviors.