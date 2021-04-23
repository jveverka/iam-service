### Deploy iam-service to Kubernetes

### Single node deployment
* Deployment
  ```
  kubectl apply -f iam-service-setup.yml
  kubectl apply -f iam-service-deployment-<platform>.yml
  ```
* Undeployment
  ```
  kubectl delete deployment.apps/iam-service -n iam-service
  kubectl delete service/iam-service -n iam-service
  kubectl delete secret/iam-service -n iam-service
  kubectl delete namespace/iam-service
  ```
  