# 📺 Eureka Server

This microservice acts as a Service Discovery Server, allowing all other microservices in the ecosystem to register themselves dynamically and discover each other.

---

## 🚀 Features

- Central registry for microservices (Pet Registration, Appointment, Notification, API Gateway)
- Supports dynamic service registration and lookup
- Enables load balancing and resilience via service discovery
- Provides a simple web dashboard to monitor registered services

---

## 🧬 Architecture Overview

- Language: Java 21
- Framework: Spring Boot
- Service Discovery: Eureka Server (Spring Cloud Netflix)
- Build tool: Maven

---

## 🌐 Exposed Port

| Port  | Purpose        |
|-------|----------------|
| 8761  | Eureka Web UI   |

> The dashboard allows real-time visualization of registered microservices and their statuses.

---

## 🏩 Project Structure

```
eurekaServer/
├── src/
│   ├── main/
│   │   ├── java/com/ms/eurekaserver/
│   │   │   └── EurekaServerApplication.java  # Main class to start Eureka server
│   └── resources/
│       ├── application.properties           # Eureka server configurations
└── pom.xml                                   # Maven configuration
```

---

## 🛠️ Future Improvements

- Enable service instance replication (for high availability)
- Secure registration and communication with token-based authentication
- Add monitoring integrations (e.g., Spring Boot Admin)