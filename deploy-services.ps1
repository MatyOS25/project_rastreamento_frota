# deploy-services.ps1

param (
    [Parameter(Mandatory=$true)]
    [string]$Environment,
    [string]$KubeConfig = "$HOME/.kube/config"
)

# Definir a variável de ambiente KUBECONFIG
$env:KUBECONFIG = $KubeConfig

# Lista de serviços para instalar
$services = @("rabbitmq", "elasticsearch", "api-gateway")

foreach ($service in $services) {
    Write-Host "Instalando $service no ambiente $Environment..."
    
    # Caminho para os arquivos de valores
    $defaultValues = "./kubernetes/charts/$service/values.yaml"
    $envValues = "./kubernetes/environments/$Environment/$service-values.yaml"
    
    # Comando para instalar/atualizar o serviço
    $helmCommand = "helm upgrade --install $service bitnami/$service " +
                   "-f $defaultValues -f $envValues " +
                   "--namespace $Environment --create-namespace"
    
    # Executar o comando
    Invoke-Expression $helmCommand
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Falha ao instalar $service"
        exit 1
    }
}

Write-Host "Todos os serviços foram instalados com sucesso!"