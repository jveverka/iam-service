### Deploy iam-service to Kubernetes

### Single node deployment
* Setup environment variables and secrets.
  ```
  echo -n value | base64
  ```
* Deployment
  ```
  kubectl apply -f iam-service-setup.yml
  kubectl apply -f iam-service-deployment.yml
  ```
* Undeployment
  ```
  kubectl delete deployment.apps/iam-service -n iam-service
  kubectl delete service/iam-service -n iam-service
  kubectl delete secret/iam-service -n iam-service
  kubectl delete namespace/iam-service
  ```
  