# Arquivo: TestUDP-servico-localizacao.ps1

# Função para obter a porta UDP do serviço
function Get-UDPPort {
    $service = kubectl get service servico-localizacao -o json | ConvertFrom-Json
    $nodePort = ($service.spec.ports | Where-Object { $_.protocol -eq 'UDP' }).nodePort
    return $nodePort
}

# Obter informações do serviço
$udpIp = "127.0.0.1"
$udpPort = Get-UDPPort

Write-Host "IP para teste: $udpIp"
Write-Host "Porta UDP: $udpPort"

# Função para gerar dados de rastreio aleatórios
function Get-RandomRastreioData {
    param (
        [string]$macAddress
    )
    $latitude = [math]::Round((Get-Random -Minimum -90000000 -Maximum 90000000) / 1000000, 6)
    $longitude = [math]::Round((Get-Random -Minimum -180000000 -Maximum 180000000) / 1000000, 6)
    $altitude = Get-Random -Minimum 0 -Maximum 1000
    $velocidade = Get-Random -Minimum 0 -Maximum 120
    $direcoes = @("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    $direcao = $direcoes | Get-Random
    $statusVeiculo = "Normal"
    $timestamp = [int64](Get-Date -UFormat %s)

    return "$macAddress,$latitude,$longitude,$altitude,$velocidade,$direcao,$statusVeiculo,$timestamp"
}

# Função para enviar mensagem UDP
function Send-UDPMessage {
    param (
        [string]$message
    )
    try {
        $endpoint = New-Object System.Net.IPEndPoint ([System.Net.IPAddress]::Parse($udpIp), $udpPort)
        $udpclient = New-Object System.Net.Sockets.UdpClient
        $bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
        $udpclient.Send($bytes, $bytes.Length, $endpoint)
        $udpclient.Close()
        Write-Host "Mensagem UDP enviada: $message"
    } catch {
        Write-Host "Erro ao enviar mensagem UDP: $_"
    }
}

# Lista de 10 MACs conhecidos para os caminhões
$caminhoes = @(
    "00:1A:2B:3C:4D:5E",
    "AA:BB:CC:DD:EE:FF",
    "11:22:33:44:55:66",
    "AA:11:BB:22:CC:33",
    "DD:EE:FF:00:11:22",
    "33:44:55:66:77:88",
    "99:88:77:66:55:44",
    "AB:CD:EF:01:23:45",
    "67:89:AB:CD:EF:01",
    "23:45:67:89:AB:CD"
)

# Configurações da simulação
$duracaoSimulacao = 300 # duração total da simulação em segundos (5 minutos)
$intervaloEnvio = 1 # intervalo entre envios em segundos

$tempoInicio = Get-Date

Write-Host "Iniciando simulação de rastreamento com 10 caminhões..."

while ((Get-Date) -lt $tempoInicio.AddSeconds($duracaoSimulacao)) {
    foreach ($caminhao in $caminhoes) {
        $message = Get-RandomRastreioData -macAddress $caminhao
        Send-UDPMessage -message $message
        Start-Sleep -Milliseconds 100 # Pequeno intervalo entre envios de diferentes caminhões
    }
    Start-Sleep -Seconds $intervaloEnvio
}

Write-Host "Simulação concluída."

# Obter logs do pod
#Write-Host "`nLogs do pod:"
#kubectl logs -l app.kubernetes.io/name=servico-localizacao --tail=50

# Descrever o pod para verificar a configuração
#Write-Host "`nDescrição do pod:"
#kubectl describe pod -l app.kubernetes.io/name=servico-localizacao