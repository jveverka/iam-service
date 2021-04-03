### Deploy to Kubernetes
Kubernetes deployment
```
kubectl apply -f iam-service-deployment-<platform>.yml
```
Undeploy from kubernetes
```
kubectl delete service/iam-service -n iam-service
kubectl delete deployment.apps/iam-service -n iam-service
kubectl delete namespace/iam-service
```