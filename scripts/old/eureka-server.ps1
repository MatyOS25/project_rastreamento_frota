# script-deploy-eureka.ps1

# Definir variáveis
$IMAGE_NAME = "eureka-server"
$IMAGE_TAG = "latest"
$DEPLOYMENT_FILE = ".\kubernetes\eureka-server-deployment.yaml"
$SERVICE_FILE = ".\kubernetes\eureka-server-service.yaml"


function Check-LastExitCode {
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Último comando falhou com código de saída $LASTEXITCODE"
        exit $LASTEXITCODE
    }
}

# Navegar para o diretório do projeto Eureka Server
Set-Location -Path "..\services\eureka-server"

#.\mvnw clean package
 #Check-LastExitCode

# Construir a imagem Docker
Write-Host "Construindo a imagem Docker do Eureka Server..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
Check-LastExitCode

# Verificar se a imagem foi criada
docker images ${IMAGE_NAME}:${IMAGE_TAG}
Check-LastExitCode

if (Get-Command minikube -ErrorAction SilentlyContinue) {
    Write-Host "Carregando imagem do Eureka Server para o Minikube..."
    minikube image load ${IMAGE_NAME}:${IMAGE_TAG}
    Check-LastExitCode
}

# Voltar para o diretório raiz do projeto
Set-Location -Path "..\..\"

# Aplicar o Deployment
Write-Host "Aplicando o Deployment do Eureka Server..."
kubectl apply -f $DEPLOYMENT_FILE
Check-LastExitCode

# Aplicar o Service
Write-Host "Aplicando o Service do Eureka Server..."
kubectl apply -f $SERVICE_FILE
Check-LastExitCode

# Verificar o status do Deployment
Write-Host "Verificando o status do Deployment do Eureka Server..."
kubectl get deployments
kubectl get pods
kubectl get services

# Expor o serviço usando Minikube
Write-Host "Expondo o serviço do Eureka Server..."
minikube service eureka-server --url

Write-Host "Processo de implantação do Eureka Server concluído!"
Write-Host "Você pode acessar o Eureka Server usando a URL acima."