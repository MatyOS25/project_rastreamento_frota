# Script PowerShell para instalar serviços específicos via Helm ou kubectl

# Definir variáveis
$servicosJsonPath = ".\servicos.json"

# Função para instalar ou atualizar um serviço
function Install-OrUpgradeService {
    param (
        [string]$nome,
        [string]$tipo,
        [string]$chart,
        [string]$versao = $null,
        [string]$valoresPersonalizados = $null,
        [string]$configMap = $null,
        [string]$comando = $null,
        [string]$arquivo = $null
    )
    
    Write-Host "Instalando/atualizando servico: $nome" -ForegroundColor Cyan
    
    if ($tipo -eq "kubectl") {
        $kubectlCommand = "kubectl $comando -f $arquivo"
        Invoke-Expression $kubectlCommand
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Recurso $nome aplicado com sucesso." -ForegroundColor Green
        } else {
            Write-Host "Erro ao aplicar recurso $nome." -ForegroundColor Red
        }
    } elseif ($tipo -eq "helm") {
        $helmCommand = "helm upgrade --install $nome $chart"
        
        if ($versao) {
            $helmCommand += " --version $versao"
        }
        
        if ($valoresPersonalizados) {
            $helmCommand += " -f $valoresPersonalizados"
        }
        
        Invoke-Expression $helmCommand
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Servico $nome instalado/atualizado com sucesso." -ForegroundColor Green
            
            if ($configMap) {
                Write-Host "Aplicando ConfigMap para $nome" -ForegroundColor Cyan
                kubectl apply -f $configMap
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "ConfigMap para $nome aplicado com sucesso." -ForegroundColor Green
                } else {
                    Write-Host "Erro ao aplicar ConfigMap para $nome." -ForegroundColor Red
                }
            }
        } else {
            Write-Host "Erro ao instalar/atualizar servico $nome." -ForegroundColor Red
        }
    } else {
        Write-Host "Tipo de instalacao desconhecido para $nome`: $tipo" -ForegroundColor Red
    }
}

# Verificar se o Helm está instalado
if (!(Get-Command helm -ErrorAction SilentlyContinue)) {
    Write-Host "Helm nao esta instalado. Por favor, instale o Helm e tente novamente." -ForegroundColor Red
    exit 1
}

# Verificar se o arquivo JSON existe
if (!(Test-Path $servicosJsonPath)) {
    Write-Host "Arquivo de servicos nao encontrado: $servicosJsonPath" -ForegroundColor Red
    exit 1
}

# Ler a lista de serviços do arquivo JSON
$servicos = Get-Content $servicosJsonPath | ConvertFrom-Json

# Adicionar repositórios Helm necessários
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# Instalar/atualizar cada serviço
foreach ($servico in $servicos) {
    Install-OrUpgradeService -nome $servico.Nome -tipo $servico.Tipo -chart $servico.Chart -versao $servico.Versao -valoresPersonalizados $servico.ValoresPersonalizados -configMap $servico.ConfigMap -comando $servico.Comando -arquivo $servico.Arquivo
}

.\need_to_run.ps1

# Verificar o status dos pods
Write-Host "`nVerificando o status dos pods:" -ForegroundColor Cyan
kubectl get pods

# Verificar o status dos serviços
Write-Host "`nVerificando o status dos servicos:" -ForegroundColor Cyan
kubectl get services

Write-Host "`nInstalacao concluida!" -ForegroundColor Green