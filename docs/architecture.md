# Architecture

The API Gateway is the single client entry point. Eureka provides discovery, while Config Server reads shared and service-specific YAML from `config-repo`.

Business services are independently deployable. Orders coordinate the business workflow, Inventory owns the Mongo catalog, Payment uses a replaceable provider boundary, and Notification uses a replaceable notifier boundary. Kafka is the event transport for asynchronous workflow steps.

Local infrastructure is defined in `infra/docker-compose.yml`. Kubernetes deployment placeholders live under `infra/k8s` and can be specialized as the service APIs are implemented.