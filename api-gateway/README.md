# 🚪 API Gateway Microservice

This microservice acts as the single entry point for all clients, routing external requests to the appropriate internal microservices.

---

## 🚀 Features

- Centralizes external API access
- Routes requests to corresponding microservices (Cadastro, Agendamento, Notificação)
- Integrates with Eureka for dynamic service discovery
- Configures CORS policies
- Exposes OpenAPI (Swagger) documentation endpoints for each service

---

## 💪 Responsibilities

- Proxy incoming HTTP requests based on path patterns
- Forward requests to backend microservices discovered via Eureka
- Ensure loose coupling between clients and internal services
- Handle basic fallback or routing errors (can be extended with filters)

---

## 📊 Routing Overview

| Path Prefix          | Destination Microservice |
|----------------------|---------------------------|
| `/cadastro/**`       | Pet Registration Service  |
| `/agendamento/**`    | Appointment Service       |
| `/notificacao/**`    | Notification Service      |

> Requests are dynamically routed using service IDs registered in Eureka.

---

## 🧱 Architecture Overview

- Language: Java 21
- Framework: Spring Boot + Spring Cloud Gateway
- Service Discovery: Eureka
- Build Tool: Maven

---

## 📆 Project Structure

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/com/ms/apigateway/
│   │   │   └── config/           # Gateway route configurations
│   │   └── resources/
│   │       ├── application.properties  # Gateway and Eureka settings
└── pom.xml                  # Maven configuration
```

---

## 🔧 Future Improvements

- Add authentication and authorization filters (e.g., JWT security)
- Implement custom fallback/error handling for routes
- Rate limiting and API throttling
- Logging and metrics for request tracing

---

# 📊 Important Notes

- Ensure Eureka Server is up before starting the API Gateway.
- All route definitions are dynamic (no hardcoded service URLs).
- This microservice does **not** contain business logic, only routing and filtering.
