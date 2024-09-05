# Obter o IP do Minikube
$minikubeIp = minikube ip
Write-Host "IP do Minikube: $minikubeIp"

# Obter informações do serviço
$serviceInfo = kubectl get service servico-localizacao -o jsonpath='{.spec.ports[*].nodePort}'
$ports = $serviceInfo -split " "
$httpPort = $ports[0]
$udpPort = $ports[1]
Write-Host "Porta HTTP NodePort: $httpPort"
Write-Host "Porta UDP NodePort: $udpPort"

# Exibir informações completas do serviço
Write-Host "`nInformações completas do serviço:"
kubectl get service servico-localizacao -o wide

# Enviar mensagem UDP
$message = "Teste de mensagem UDP via Kubernetes"
$endpoint = New-Object System.Net.IPEndPoint ([System.Net.IPAddress]::Parse($minikubeIp), $udpPort)
$udpclient = New-Object System.Net.Sockets.UdpClient
$bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
$udpclient.Send($bytes, $bytes.Length, $endpoint)
$udpclient.Close()
Write-Host "`nMensagem enviada para ${minikubeIp}:$udpPort"

# Aguardar um pouco para a mensagem ser processada
Start-Sleep -Seconds 2

# Obter logs do pod
Write-Host "`nLogs do pod:"
kubectl logs -l app.kubernetes.io/name=servico-localizacao --tail=20

# Descrever o pod para verificar a configuração
Write-Host "`nDescrição do pod:"
kubectl describe pod -l app.kubernetes.io/name=servico-localizacao