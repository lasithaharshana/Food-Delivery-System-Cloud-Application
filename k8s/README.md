Kubernetes manifests are split per component under `k8s/`. Apply them in this order:

1. `kubectl apply -f k8s/mysql/`
2. `kubectl apply -f k8s/kafka/`
3. `kubectl apply -f k8s/connect/`
4. `kubectl apply -f k8s/services/`


