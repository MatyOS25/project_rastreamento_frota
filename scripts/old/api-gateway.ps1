# script-deploy-api-gateway.ps1

# Definir variáveis
$IMAGE_NAME = "api-gateway"
$IMAGE_TAG = "latest"
$DEPLOYMENT_FILE = ".\kubernetes\api-gateway-deployment.yaml"
$SERVICE_FILE = ".\kubernetes\api-gateway-service.yaml"


function Check-LastExitCode {
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Último comando falhou com código de saída $LASTEXITCODE"
        exit $LASTEXITCODE
    }
}

# Navegar para o diretório do projeto API Gateway
Set-Location -Path "..\services\api-gateway"

#.\mvnw clean package
 #Check-LastExitCode

# Construir a imagem Docker
Write-Host "Construindo a imagem Docker do API Gateway..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
Check-LastExitCode

# Verificar se a imagem foi criada
docker images ${IMAGE_NAME}:${IMAGE_TAG}
Check-LastExitCode


if (Get-Command minikube -ErrorAction SilentlyContinue) {
    Write-Host "Carregando imagem do API Gateway para o Minikube..."
    minikube image load ${IMAGE_NAME}:${IMAGE_TAG}
    Check-LastExitCode
}

# Voltar para o diretório raiz do projeto
Set-Location -Path "..\..\"

# Aplicar o Deployment
Write-Host "Aplicando o Deployment do API Gateway..."
kubectl apply -f $DEPLOYMENT_FILE
Check-LastExitCode

# Aplicar o Service
Write-Host "Aplicando o Service do API Gateway..."
kubectl apply -f $SERVICE_FILE
Check-LastExitCode

# Verificar o status do Deployment
Write-Host "Verificando o status do Deployment do API Gateway..."
kubectl get deployments
kubectl get pods
kubectl get services

# Expor o serviço usando Minikube
Write-Host "Expondo o serviço do API Gateway..."
minikube service api-gateway --url

Write-Host "Processo de implantação do API Gateway concluído!"
Write-Host "Você pode acessar o API Gateway usando a URL acima."