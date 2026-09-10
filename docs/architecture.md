# Architecture

The API Gateway is the single client entry point. Eureka provides discovery, while Config Server reads shared and service-specific YAML from `config-repo`.

Business services are independently deployable. Orders coordinate the workflow and persist an append-only order event log in PostgreSQL. Inventory owns the Mongo catalog, while Payment and Notification persist their local state in PostgreSQL. Each service owns its data and communicates through the `ecommerce.events` Kafka topic.

The order Saga is asynchronous: `ORDER_CREATED` is consumed by Inventory, `INVENTORY_RESERVED` triggers `PAYMENT_REQUESTED`, and Payment emits `PAYMENT_AUTHORIZED` or `PAYMENT_FAILED`. Order updates its state and Notification records terminal outcomes. Kafka consumer groups and durable offsets allow events to remain queued while a downstream service is temporarily unavailable, preventing lost orders.

The services expose OAuth2/JWT resource-server protection, with the issuer/JWK endpoint supplied through `JWT_JWK_SET_URI`. Resilience4j protects payment processing with a circuit breaker and retry policy; compensation is represented by `INVENTORY_REJECTED` and `PAYMENT_FAILED` events.

Local infrastructure is defined in `infra/docker-compose.yml`. Kubernetes backing services are in `infra/k8s/infrastructure.yml`; apply that file before the application deployments, which all use the `ecommerce` namespace.