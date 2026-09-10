# Kubernetes manifests

Apply `infrastructure.yml` and `secrets.yml` first, then the application manifests
and `scaling.yml`. All resources use the `ecommerce` namespace. Replace the
placeholder secret values before applying to a real cluster. Images are expected
to be built from the repository root, for example
`docker build -f order-service/Dockerfile -t ecommerce/order-service:latest .`.

Each service directory contains a baseline Deployment and ClusterIP Service. Replace the image tags and add secrets, probes, persistence, and resource limits before production deployment.