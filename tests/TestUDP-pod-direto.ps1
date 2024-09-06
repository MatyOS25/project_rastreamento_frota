$serviceUrl = (minikube service servico-localizacao --url -n default --https=false | Select-String -Pattern "udp://").ToString().Trim()
$serviceUrl -match "udp://(?<ip>.+):(?<port>\d+)"
$ip = $Matches.ip
$port = [int]$Matches.port

$message = "Teste de mensagem UDP para o Minikube"
$udpClient = New-Object System.Net.Sockets.UdpClient

try {
    $bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
    $udpClient.Send($bytes, $bytes.Length, $ip, $port)
    Write-Host "Mensagem enviada com sucesso para ${ip}:${port}"
}
catch {
    Write-Host "Erro ao enviar a mensagem: $_"
}
finally {
    $udpClient.Close()
}