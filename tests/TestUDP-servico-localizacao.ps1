# Arquivo: TestUDP-servico-localizacao.ps1

# Usar a URL fornecida pelo Minikube
$minikubeUrl = "127.0.0.1"
$udpPort = 57046  # Use a segunda porta fornecida pelo comando minikube service

Write-Host "IP para teste: $minikubeUrl"
Write-Host "Porta UDP: $udpPort"

# Exibir informações completas do serviço
Write-Host "`nInformações completas do serviço:"
kubectl get service servico-localizacao -o wide

# Enviar mensagem UDP
$message = "Teste de mensagem UDP via Kubernetes"
try {
    $endpoint = New-Object System.Net.IPEndPoint ([System.Net.IPAddress]::Parse($minikubeUrl), $udpPort)
    $udpclient = New-Object System.Net.Sockets.UdpClient
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
    $udpclient.Send($bytes, $bytes.Length, $endpoint)
    $udpclient.Close()
    Write-Host "`nMensagem UDP enviada para ${minikubeUrl}:$udpPort"
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