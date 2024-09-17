#Ja coloquei pra rodar junto do deploy_with_helm.ps1
kubectl exec -it rabbitmq-0 -- rabbitmqctl change_password admin adminnew123
kubectl apply -f ./kubernetes/role.yaml
kubectl apply -f ./kubernetes/rolebinding.yaml

