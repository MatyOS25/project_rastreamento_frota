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

# Exibir informações completas do serviço
Write-Host "`nInformações completas do serviço:"
kubectl get service servico-localizacao -o wide

# Enviar mensagem UDP
$message = "Teste de mensagem UDP via Kubernetes"
try {
    $endpoint = New-Object System.Net.IPEndPoint ([System.Net.IPAddress]::Parse($udpIp), $udpPort)
    $udpclient = New-Object System.Net.Sockets.UdpClient
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
    $udpclient.Send($bytes, $bytes.Length, $endpoint)
    $udpclient.Close()
    Write-Host "`nMensagem UDP enviada para ${udpIp}:$udpPort"
} catch {
    Write-Host "Erro ao enviar mensagem UDP: $_"
}

# Aguardar um pouco para a mensagem ser processada
Start-Sleep -Seconds 2

# Obter logs do pod
Write-Host "`nLogs do pod:"
kubectl logs -l app.kubernetes.io/name=servico-localizacao --tail=20

# Descrever o pod para verificar a configuração
Write-Host "`nDescrição do pod:"
kubectl describe pod -l app.kubernetes.io/name=servico-localizacao