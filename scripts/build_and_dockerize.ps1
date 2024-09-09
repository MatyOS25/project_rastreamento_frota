
param(
    [Parameter(Mandatory=$true)]
    [string]$ServiceName
)

# Definir variáveis
$IMAGE_NAME = $ServiceName
$IMAGE_TAG = "latest"

Set-Location -Path "..\services\$ServiceName"

if (-not (Test-Path .)) {
    Write-Error "O diretório do serviço '$ServiceName' não foi encontrado."
    exit 1
}

Write-Host "Empacotando o projeto $ServiceName com Maven..."
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao empacotar o projeto Maven"
    exit $LASTEXITCODE
}

Write-Host "Construindo a imagem Docker..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falha ao construir a imagem Docker"
    exit $LASTEXITCODE
}

docker images ${IMAGE_NAME}:${IMAGE_TAG}

Set-Location -Path "..\..\"

Write-Host "Processo concluído. A imagem Docker '${IMAGE_NAME}:${IMAGE_TAG}' foi criada com sucesso."

# Opcional: Fazer push da imagem para um registro Docker, se necessário
# docker push ${IMAGE_NAME}:${IMAGE_TAG}

Write-Host "Para implantar no Kubernetes do Docker Desktop, use:"
Write-Host "kubectl apply -f seu-arquivo-de-deployment.yaml"
Write-Host "kubectl apply -f seu-arquivo-de-service.yaml"