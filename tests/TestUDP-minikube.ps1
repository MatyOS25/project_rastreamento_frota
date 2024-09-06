# Obter o IP do Minikube
$minikubeIp = minikube ip

if (-not $minikubeIp) {
    Write-Host "Não foi possível obter o IP do Minikube."
    exit
}

# Definir a mensagem e a porta
$message = "Teste de mensagem UDP para o Minikube"
$port = 32607  # Use a porta NodePort do seu serviço UDP

# Criar um cliente UDP
$udpClient = New-Object System.Net.Sockets.UdpClient

try {
    # Converter a mensagem em bytes
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($message)

    # Enviar a mensagem
    $udpClient.Send($bytes, $bytes.Length, $minikubeIp, $port)

    Write-Host "Mensagem enviada com sucesso para ${minikubeIp}:${port}"
}
catch {
    Write-Host "Erro ao enviar a mensagem: $_"
}
finally {
    # Fechar o cliente UDP
    $udpClient.Close()
}