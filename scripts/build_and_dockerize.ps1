# Script para empacotar o projeto Maven, configurar o ambiente Docker do Minikube e criar a imagem Docker

param(
    [Parameter(Mandatory=$true)]
    [string]$ServiceName
)

# Definir variáveis
$IMAGE_NAME = $ServiceName
$IMAGE_TAG = "latest"

# Navegar para o diretório do projeto (ajuste o caminho conforme necessário)
Set-Location -Path "..\services\$ServiceName"

# Verificar se o diretório existe
if (-not (Test-Path .)) {
    Write-Error "O diretório do serviço '$ServiceName' não foi encontrado."
    exit 1
}

# Empacotar o projeto com Maven
Write-Host "Empacotando o projeto $ServiceName com Maven..."
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao empacotar o projeto Maven"
    exit $LASTEXITCODE
}

# Configurar o ambiente Docker para usar o Minikube
Write-Host "Configurando o ambiente Docker para usar o Minikube..."
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Construir a imagem Docker no contexto do Minikube
Write-Host "Construindo a imagem Docker no Minikube..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao construir a imagem Docker"
    exit $LASTEXITCODE
}

# Verificar se a imagem foi criada
docker images ${IMAGE_NAME}:${IMAGE_TAG}

# Voltar para o diretório raiz do projeto
Set-Location -Path "..\..\"

Write-Host "Processo concluído. A imagem Docker '${IMAGE_NAME}:${IMAGE_TAG}' foi criada com sucesso no Minikube."
