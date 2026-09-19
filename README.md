# E-Commerce Microservices

A distributed, event-driven e-commerce backend built with **Java 21, Spring Boot, Spring Cloud, Apache Kafka, PostgreSQL, MongoDB, JWT, Docker, and Kubernetes**.

The platform separates core business capabilities into independently deployable microservices and uses an **event-driven Saga workflow** to coordinate order, inventory, payment, and notification processing without distributed transactions.

## Architecture

```text
                         ┌─────────────────────┐
                         │     API Gateway      │
                         │      :8080           │
                         │   JWT Authentication │
                         └──────────┬──────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
      ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
      │ Order Service │     │   Inventory   │     │    Payment    │
      │    :8081      │     │    :8082      │     │    :8083      │
      └───────┬───────┘     └───────┬───────┘     └───────┬───────┘
              │                     │                     │
              │                     │                     │
              └─────────────────────┼─────────────────────┘
                                    │
                              Apache Kafka
                           ecommerce.events
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ Notification Service│
                         │        :8084        │
                         └─────────────────────┘

Supporting Infrastructure:
Eureka :8761 | Config Server :8888 | PostgreSQL | MongoDB
Prometheus :9090 | Zipkin :9411
```

## Microservices

| Service              | Port | Responsibility                        | Storage           |
| -------------------- | ---: | ------------------------------------- | ----------------- |
| Eureka Server        | 8761 | Service discovery                     | —                 |
| Config Server        | 8888 | Centralized configuration             | Git/native config |
| API Gateway          | 8080 | API routing and JWT authentication    | —                 |
| Order Service        | 8081 | Order creation and Saga orchestration | PostgreSQL        |
| Inventory Service    | 8082 | Inventory/catalog processing          | MongoDB           |
| Payment Service      | 8083 | Payment authorization                 | PostgreSQL        |
| Notification Service | 8084 | Notification processing               | PostgreSQL        |

## Technology Stack

* **Java 21**
* **Spring Boot 3.5**
* **Spring Cloud**
* **Spring Cloud Gateway**
* **Spring Cloud Netflix Eureka**
* **Spring Cloud Config**
* **Spring Security**
* **JWT / HMAC-SHA256**
* **Apache Kafka 4.0**
* **PostgreSQL 16**
* **MongoDB 7**
* **Resilience4j**
* **Docker / Docker Compose**
* **Kubernetes**
* **Prometheus**
* **Zipkin**
* **Maven**

## Event-Driven Saga

The order workflow follows an event-driven Saga pattern.

```text
POST /api/orders
       │
       ▼
 ORDER_CREATED
       │
       ▼
Inventory Service
       │
       ├── INVENTORY_RESERVED
       │          │
       │          ▼
       │    PAYMENT_REQUESTED
       │          │
       │          ▼
       │    Payment Service
       │          │
       │          ▼
       │    PAYMENT_AUTHORIZED
       │          │
       │          ▼
       │       CONFIRMED
       │
       └── INVENTORY_REJECTED
                  │
                  ▼
              CANCELLED
```

If payment processing fails temporarily, the `PAYMENT_REQUESTED` event remains available through Kafka processing and can be processed when the Payment Service becomes available again.

## Saga Event Flow

The main events are:

```text
ORDER_CREATED
      ↓
INVENTORY_RESERVED
      ↓
PAYMENT_REQUESTED
      ↓
PAYMENT_AUTHORIZED
      ↓
ORDER CONFIRMED
```

Failure events can result in:

```text
INVENTORY_REJECTED → ORDER CANCELLED

PAYMENT_FAILED → ORDER CANCELLED
```

## Outbox Pattern

The Order Service uses an outbox-style event mechanism.

When an order is created:

1. The order is persisted.
2. The corresponding event is persisted in the order event/outbox table.
3. A background publisher publishes unpublished events to Kafka.
4. After successful publication, the event is marked as published.
5. If Kafka is temporarily unavailable, the event remains unpublished and can be retried.

This helps prevent losing an event between database persistence and Kafka publication.

## Kafka

Kafka topic:

```text
ecommerce.events
```

Consumer groups:

```text
order-saga
inventory-saga
payment-saga
notification-saga
```

Kafka enables asynchronous communication between the services and decouples the individual business components.

## JWT Security

The application uses JWT-based authentication without Keycloak.

The API Gateway provides:

```text
POST /api/auth/login
```

Example request:

```json
{
  "username": "<configured-username>",
  "password": "<configured-password>"
}
```

A successful login returns a Bearer JWT.

Protected APIs require:

```text
Authorization: Bearer <JWT_TOKEN>
```

Authentication is handled using an HMAC-based JWT configuration shared by the gateway and backend services.

The following endpoints are publicly accessible:

```text
/api/auth/**
/actuator/health
/actuator/info
```

Business APIs require authentication.

> For production deployments, store JWT secrets and credentials in environment variables or a secure secrets manager rather than committing them to source control.

## API Endpoints

### Authentication

```text
POST /api/auth/login
```

Example:

```json
{
  "username": "<configured-username>",
  "password": "<configured-password>"
}
```

### Orders

```text
POST /api/orders
GET  /api/orders
GET  /api/orders/{orderId}
```

Create an order:

```json
{
  "total": 100.00
}
```

Example response:

```json
{
  "id": "order-id",
  "total": 100.00,
  "status": "PENDING_INVENTORY",
  "createdAt": "2026-09-17T18:26:57.570477Z"
}
```

After successful Saga processing:

```text
PENDING_INVENTORY
       ↓
PENDING_PAYMENT
       ↓
CONFIRMED
```

## Project Structure

```text
ecommerce-microservices/
│
├── shared-contracts/
│
├── eureka-server/
│
├── config-server/
│
├── api-gateway/
│
├── order-service/
│
├── inventory-service/
│
├── payment-service/
│
├── notification-service/
│
├── config-repo/
│
├── infra/
│   ├── docker-compose.yml
│   └── k8s/
│
├── pom.xml
└── README.md
```

## Local Infrastructure

Start Docker infrastructure from the project root:

```powershell
cd "C:\Users\DELL\Downloads\ecommerce-microservices"

docker compose -f infra/docker-compose.yml up -d
```

Check containers:

```powershell
docker ps
```

The infrastructure includes:

```text
Kafka          9092
PostgreSQL     5432
PostgreSQL     5433
PostgreSQL     5434
MongoDB        27017
Prometheus     9090
Zipkin         9411
```

## Build

Set Java 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.7"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Verify:

```powershell
java -version
mvn -version
```

Build the complete project:

```powershell
mvn clean install -DskipTests
```

The complete Maven reactor should build:

```text
ecommerce-microservices
shared-contracts
eureka-server
config-server
api-gateway
order-service
inventory-service
payment-service
notification-service
```

## Starting the Application

Start services in this general order:

```text
1. Docker infrastructure
2. Eureka Server
3. Config Server
4. API Gateway
5. Order Service
6. Inventory Service
7. Payment Service
8. Notification Service
```

### Eureka

```text
http://localhost:8761
```

### Config Server

```text
http://localhost:8888
```

### API Gateway

```text
http://localhost:8080
```

### Actuator Health

```text
http://localhost:8080/actuator/health
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
http://localhost:8084/actuator/health
```

## Running Multiple Order Service Instances

The services are registered with Eureka and can run as multiple instances.

For example, start the normal Order Service:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.7"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

cd "C:\Users\DELL\Downloads\ecommerce-microservices\order-service"

mvn spring-boot:run
```

Start another instance on port `8091`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.7"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

$env:SERVER_PORT = "8091"

cd "C:\Users\DELL\Downloads\ecommerce-microservices\order-service"

mvn spring-boot:run
```

Both instances register with Eureka:

```text
ORDER-SERVICE :8081
ORDER-SERVICE :8091
```

This allows the Order Service to be independently scaled without changing the other business services.

## Payment Failure and Recovery

The system was tested with the Payment Service temporarily unavailable.

The verified workflow was:

```text
1. Payment Service stopped
          ↓
2. New order created
          ↓
3. Order persisted successfully
          ↓
4. Inventory processing completed
          ↓
5. Order reached PENDING_PAYMENT
          ↓
6. PAYMENT_REQUESTED remained available
          ↓
7. Payment Service restarted
          ↓
8. Pending payment processed
          ↓
9. PAYMENT_AUTHORIZED emitted
          ↓
10. Order became CONFIRMED
```

This verifies the required failure-recovery behavior:

> A temporary Payment Service outage does not cause the order to be lost. The pending workflow can continue after Payment Service recovery.

## Resilience

Resilience4j is included for payment processing with:

* Retry
* Circuit Breaker

Kafka consumers also use retry handling so that failed event processing can be retried instead of immediately losing the event.

## Observability

### Prometheus

```text
http://localhost:9090
```

### Zipkin

```text
http://localhost:9411
```

Actuator endpoints provide service health and operational information.

## Kubernetes

Kubernetes manifests are available under:

```text
infra/k8s/
```

Apply infrastructure first:

```powershell
kubectl apply -f infra/k8s/infrastructure.yml
```

Then deploy the application manifests:

```powershell
kubectl apply -f infra/k8s/
```

Check deployments:

```powershell
kubectl get deployments
```

Check pods:

```powershell
kubectl get pods
```

Check services:

```powershell
kubectl get services
```

The architecture supports independently scaling a service, for example:

```powershell
kubectl scale deployment order-service --replicas=3
```

The exact Kubernetes deployment/service names should be verified with:

```powershell
kubectl get deployments
```

## Verification Results

The following project validations were completed successfully:

### Clean Saga

Verified event sequence:

```text
ORDER_CREATED
INVENTORY_RESERVED
PAYMENT_REQUESTED
PAYMENT_AUTHORIZED
```

Final order status:

```text
CONFIRMED
```

### Payment Failure and Recovery

Verified:

```text
Payment Service unavailable
        ↓
Order remains persisted
        ↓
Payment request remains pending
        ↓
Payment Service recovers
        ↓
Payment authorized
        ↓
Order confirmed
```

### Independent Scaling

Verified two Order Service instances running simultaneously:

```text
ORDER-SERVICE :8081
ORDER-SERVICE :8091
```

Both instances were registered with Eureka and accessible through the API Gateway.

### Kafka

Verified:

```text
Topic:
ecommerce.events

Consumer Groups:
order-saga
inventory-saga
payment-saga
notification-saga
```

Kafka consumer groups were active with no current lag during verification.

### Maven Build

Final project validation:

```text
mvn clean install -DskipTests
```

Result:

```text
BUILD SUCCESS
```

All nine Maven modules built successfully.

## Expected Business Impact

### Independent Scaling

Each business service is independently deployable and discoverable through Eureka.

For high traffic scenarios such as Black Friday:

```text
Order Service
     ↓
multiple instances
     ↓
traffic handled independently
```

Other services do not need to be scaled just because Order traffic increases.

### Payment Failure Recovery

The event-driven architecture separates order creation from payment processing.

Therefore:

```text
Order created
     ↓
Payment temporarily unavailable
     ↓
Pending payment event retained
     ↓
Payment service recovers
     ↓
Payment processed
     ↓
Order confirmed
```

This prevents temporary Payment Service downtime from directly causing the order workflow to be lost.

## Git

The project is maintained using Git.

Current main branch validation:

```text
Working tree: clean
Branch: main
Remote: origin/main
```

Latest implementation includes:

```text
JWT security
Event-driven Saga
Kafka integration
Outbox event publishing
Payment recovery
Independent service scaling
Docker infrastructure
Kubernetes manifests
Observability infrastructure
```

## Future Improvements

Possible future enhancements include:

* Real payment provider integration
* Real notification provider integration
* Persistent inventory reservation and release logic
* Distributed tracing improvements
* Production secret management
* Kubernetes ConfigMaps and Secrets
* Horizontal Pod Autoscaling
* CI/CD pipeline
* Automated integration tests
* Frontend application
* API documentation with OpenAPI/Swagger
* Production-grade Kafka replication and monitoring
