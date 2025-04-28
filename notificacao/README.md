# ✉️ Notification Microservice

This microservice is responsible for sending email notifications related to pet registrations and appointment scheduling.

---

## 🚀 Features

- Sends welcome emails when a new pet is registered.
- Sends appointment confirmation and cancellation emails.
- Sends email with buttons to confirm or cancel an appointment via secure token links.
- Listens to RabbitMQ events and reacts accordingly.

---

## 📦 REST API Endpoints

| Method | Endpoint                | Description                               |
|--------|--------------------------|-------------------------------------------|
| POST   | `/notifications/send`    | Send a custom notification (for tests)   |

---

## 🧬 Architecture Overview

- Language: Java 21
- Framework: Spring Boot
- Messaging: RabbitMQ
- API Docs: OpenAPI 3 (Swagger)
- Build tool: Maven

---

## 📨 Messaging Integration (RabbitMQ)

### Events Consumed

| Event Type            | Queue                        | Description                              |
|-----------------------|-------------------------------|------------------------------------------|
| `pet_created`         | `pet.created`                 | Sends welcome email to new pet owner    |
| `appointment_created` | `appointment.created`         | Sends appointment confirmation email    |
| `appointment_updated` | `appointment.updated`         | Sends updated appointment information   |
| `appointment_cancel.request` | `appointment_cancel.request` | Sends email to confirm cancellation    |

### Events Published

| Event Type            | Routing Key                      | Description                                          |
|-----------------------|-----------------------------------|------------------------------------------------------|
| `appointment_cancelled_send` | `send.appointment.cancelled` | Publishes appointment cancellation after email confirmation |

> ⚠️ The Notification service only publishes cancellation confirmation back to the Appointment service.

---

## 📁 DTO Responsibilities

| DTO                     | Purpose                                 |
|-------------------------|-----------------------------------------|
| `EmailDTO`              | Used for sending manual/test emails    |
| `AppointmentResponseDTO`| Used for appointment-related messages  |
| `PetEventDTO`           | Used for pet registration messages     |

---

## ✅ Good Practices Applied

- Separation of concerns between consumer and producer classes.
- HTML templates for better email formatting.
- Secure token validation on email links.
- Swagger API documentation.
- Clean and maintainable code structure.

---

## 📂 Project Structure

```
notificacao/
├── src/
│   ├── main/
│   │   ├── java/com/ms/notificacao/
│   │   │   ├── config/               # Configurations (Swagger, RabbitMQ, Mail)
│   │   │   ├── controller/           # REST controllers
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── exception/            # Custom exceptions
│   │   │   ├── messaging/            # Consumers and producers
│   │   │   ├── service/              # Business logic (email sending)
│   └── resources/
│       ├── templates/                # HTML templates for emails
│       ├── application.properties    # Configuration properties
└── pom.xml                           # Maven configuration
```

---

## 🛠️ Future Improvements

- Add retry mechanism for email delivery failures.
- Add support for SMS notifications.
- Improve email templates with branding.
- Add unit and integration tests.
