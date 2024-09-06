$message = "Teste de mensagem UDP"
$endpoint = New-Object System.Net.IPEndPoint ([System.Net.IPAddress]::Parse("127.0.0.1"), 9876)
$udpclient = New-Object System.Net.Sockets.UdpClient
$bytes = [System.Text.Encoding]::ASCII.GetBytes($message)
$udpclient.Send($bytes, $bytes.Length, $endpoint)
$udpclient.Close()
Write-Host "Mensagem UDP enviada para 127.0.0.1:9876"