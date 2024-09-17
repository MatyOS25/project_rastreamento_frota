# limpar-cluster-kubernetes.ps1

# Função para exibir mensagens de log
function Log-Message {
    param([string]$message)
    Write-Host "`n[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $message" -ForegroundColor Cyan
}

# Remover Helm releases
Log-Message "Removendo Helm releases..."
helm list --all-namespaces --short | ForEach-Object { 
    $namespace = (helm status $_ -o json | ConvertFrom-Json).namespace
    Log-Message "Desinstalando release $_ do namespace $namespace"
    helm uninstall $_ --namespace $namespace
}

# Listar todos os namespaces
$namespaces = kubectl get namespaces -o jsonpath='{.items[*].metadata.name}' | ConvertFrom-String

# Remover recursos de cada namespace (exceto os do sistema)
foreach ($namespace in $namespaces) {
    if ($namespace -notin @("kube-system", "kube-public", "kube-node-lease")) {
        Log-Message "Removendo recursos do namespace: $namespace"
        kubectl delete all --all -n $namespace
        kubectl delete pvc --all -n $namespace
        kubectl delete configmap --all -n $namespace
        kubectl delete secret --all -n $namespace
        kubectl delete ingress --all -n $namespace
    }
}

# Remover namespaces personalizados
foreach ($namespace in $namespaces) {
    if ($namespace -notin @("default", "kube-system", "kube-public", "kube-node-lease")) {
        Log-Message "Removendo namespace: $namespace"
        kubectl delete namespace $namespace
    }
}

# Remover PersistentVolumes
Log-Message "Removendo PersistentVolumes..."
kubectl delete pv --all

# Verificar recursos remanescentes
Log-Message "Verificando recursos remanescentes..."
kubectl get all --all-namespaces

Log-Message "Limpeza concluída. Verifique os recursos acima para garantir que tudo foi removido corretamente."
Log-Message "Se necessário, reinicie o cluster Kubernetes no Docker Desktop manualmente."