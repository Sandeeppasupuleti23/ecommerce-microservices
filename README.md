# Ecommerce Microservices

Spring Boot and Spring Cloud reference platform for order, inventory, payment, and notification workflows.

## Modules

- `eureka-server`: service discovery on port 8761
- `config-server`: native configuration server on port 8888
- `api-gateway`: edge routing on port 8080
- `order-service`: order API on port 8081
- `inventory-service`: Mongo-backed catalog boundary on port 8082
- `payment-service`: mock payment provider on port 8083
- `notification-service`: mock notification provider on port 8084

## Build

```text
mvnw.cmd clean verify
```

Start local infrastructure with `docker compose -f infra/docker-compose.yml up -d`.
Run the foundation services first, then the business services from their module directories.

The order API starts a Saga with `POST /api/orders` and a JSON body such as `{"total": 49.99}`. Configure `JWT_JWK_SET_URI` when using a real identity provider. For Kubernetes, apply `infra/k8s/infrastructure.yml` first, then the application manifests.