# Arquivo: ExecutarSimuladorUDP.ps1

# Função para obter a porta UDP do serviço
function Get-UDPPort {
    $service = kubectl get service servico-localizacao -o json | ConvertFrom-Json
    $nodePort = ($service.spec.ports | Where-Object { $_.protocol -eq 'UDP' }).nodePort
    return $nodePort
}

# Obter a porta UDP
$udpPort = Get-UDPPort

if ($null -eq $udpPort) {
    Write-Host "Erro: Não foi possível obter a porta UDP do serviço."
    exit 1
}

Write-Host "Porta UDP obtida: $udpPort"

# Caminho para o arquivo .exe (ajuste conforme necessário)
$exePath = "..\aplicacoes\udp-sender\target\release\udp-sender.exe"

# Verificar se o arquivo .exe existe
if (-not (Test-Path $exePath)) {
    Write-Host "Erro: O arquivo $exePath não foi encontrado."
    exit 1
}

# Executar o .exe com a porta como argumento
Write-Host "Executando $exePath com a porta $udpPort..."
try {
    & $exePath $udpPort
}
catch {
    Write-Host "Erro ao executar o simulador: $_"
    exit 1
}

Write-Host "Simulação concluída."