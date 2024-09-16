# Atualiza o chart do servico-gerenciador-consumidor

# Atualize o chart do servico-gerenciador-consumidor
helm upgrade --install servico-gerenciador-consumidor ./kubernetes/charts/servico-gerenciador-consumidor

# Verifique o status do deployment
kubectl get deployments

# Verifique os logs do pod
$POD_NAME = kubectl get pods -l app.kubernetes.io/name=servico-gerenciador-consumidor -o jsonpath="{.items[0].metadata.name}"
kubectl logs $POD_NAME

# Verifique o status dos serviços
kubectl get services

# Verifique as permissões
kubectl get roles
kubectl get rolebindings

Write-Host "Atualização concluída. Verifique os logs acima para garantir que tudo está funcionando corretamente."

# Verifique os detalhes do serviço
kubectl describe service servico-gerenciador-consumidor

# Verifique os detalhes do deployment
kubectl describe deployment servico-gerenciador-consumidor

# Verifique os eventos do namespace
kubectl get events --sort-by=.metadata.creationTimestamp

Write-Host "Se ainda houver problemas, verifique os eventos e detalhes do serviço e deployment acima."