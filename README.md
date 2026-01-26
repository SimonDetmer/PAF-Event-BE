# EventM – Backend

## Overview

The backend of **EventM** is a Spring Boot–based REST API responsible for managing events, locations, users, orders, tickets, and concurrency control during ticket purchases.  
A key focus of the backend is ensuring **data consistency and concurrency safety** during high-demand ticket sales.

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Web (REST)
- Spring Data JPA / Hibernate
- MySQL (Dockerized)
- Maven
- Lombok
- JUnit 5 / Mockito

---

## Architecture Overview

The backend follows a layered architecture:

- **Controller Layer**  
  Handles HTTP requests and responses (REST API).

- **Service Layer**  
  Contains business logic, including ticket purchasing, state transitions, and concurrency handling.

- **Persistence Layer**  
  JPA repositories for database access.

- **Domain Model**  
  Entities such as Event, Ticket, Order, User, and Location.

---

## Ticket State Management (State Pattern)

The ticket lifecycle is implemented using the **State Pattern**.

### Ticket States
- AVAILABLE
- RESERVED
- PURCHASED
- CANCELLED (not active)
- EXPIRED (not active)

Each state encapsulates its allowed transitions, preventing invalid operations such as purchasing an already purchased ticket.

State transitions are managed via a dedicated service (`TicketStateService`), ensuring clear separation of concerns.

---

## Concurrency Handling

Concurrency is a central aspect of the backend design.

### Optimistic Locking
- The `Event` entity uses a `@Version` field.
- Each ticket-related modification increments the version automatically.

### Conflict Detection
- During order creation, the backend verifies that the event version sent by the client still matches the database version.
- If not, the order is rejected.

### Error Handling
- Concurrency conflicts result in an **HTTP 409 Conflict** response.
- Clear error messages are returned to the frontend.

### Transaction Management
- Order creation is executed inside a transactional boundary.
- If any step fails, the transaction is rolled back to maintain data integrity.

---

## API Overview (Excerpt)

- `GET /api/events` – List events
- `POST /api/orders` – Create an order
- `GET /api/tickets` – Retrieve tickets
- `GET /api/locations` – Manage event locations

---

## Testing

The backend includes:

- **Unit Tests**  
  Testing services and controllers in isolation.

- **Component Tests**  
  Verifying controller-service interactions.

- **Integration Tests**  
  Ensuring application context loads and critical workflows function correctly.

---

## Running the Backend

### Requirements
- Java 17
- Docker & Docker Compose

### Steps

1. Start the database:
   ```bash
   docker-compose up -d

2. Start the backend:
   mvn spring-boot:run

The backend will be available at:
http://localhost:8080

---

## Notes

This backend is designed for educational purposes with a strong emphasis on:
- clean architecture
- concurrency handling
- maintainable business logic