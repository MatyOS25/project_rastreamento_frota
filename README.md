# Projeto de Rastreamento de Frota

## Visão Geral

Este projeto implementa um sistema de rastreamento de frota utilizando uma arquitetura de microserviços. O sistema recebe dados de localização dos veículos via UDP e processa essas informações para fornecer insights sobre a frota.

## Arquitetura

O sistema é composto pelos seguintes serviços principais:

1. **servico-localizacao**: Recebe pacotes UDP dos veículos e os envia para uma fila RabbitMQ.
2. **servico-veiculos**: Processa as mensagens da fila, verifica a procedência dos pacotes e gerencia os dados dos veículos.
3. Outros serviços que consomem dados processados via pub/sub.

### Fluxo de Dados

1. Veículos enviam pacotes UDP com informações de localização.
2. `servico-localizacao` recebe os pacotes e os envia para o RabbitMQ.
3. `servico-veiculos` consome as mensagens, verifica a autenticidade e processa os dados.
4. Dados processados são publicados em um tópico pub/sub para outros serviços.

## Dados Processados

- Velocidade
- Direção
- Status do veículo
- Latitude
- Longitude
- Altitude

## Tecnologias Utilizadas

- UDP para comunicação com veículos
- RabbitMQ para filas de mensagens
- WebSockets para consultas diretas aos veículos (planejado)
- Kubernetes para orquestração de containers
- Helm para gerenciamento de pacotes Kubernetes

## Processo de Implantação

1. Execute o script de build
2. Navegue até a pasta `kubernetes/charts`
3. Se necessário, desinstale a versão anterior: `helm uninstall [nome-do-release]`
4. Instale/atualize o serviço: `helm upgrade --install [nome-do-release] [pasta-do-chart]`
5. Verifique os pods: `kubectl get pods`
6. Obtenha logs: `kubectl logs [id-do-pod]`
7. Acesse o serviço web: `minikube service [nome-do-serviço]`

## Instalação em um Novo PC

### Pré-requisitos

- Docker Desktop com Kubernetes ativado (ou Kubernetes nativo no Linux)
- Kubernetes Helm

### Passos

1. Certifique-se de que o contexto do kubectl está configurado para o Docker Desktop
2. Instale o Kubernetes Helm (pode ser via Chocolatey no Windows)
3. Execute o script de build na pasta `scripts` para cada serviço
4. Navegue até `kubernetes/charts`
5. Instale cada serviço: `helm install [nome-do-serviço] ./[pasta-do-serviço]`

Exemplo: `helm install eureka-server ./eureka-server`

## Notas Importantes

- O sistema utiliza UDP para comunicação rápida, priorizando a velocidade sobre a garantia de entrega.
- Nem todos os pacotes precisam ser salvos para evitar sobrecarga do servidor.
- WebSockets podem ser utilizados para consultas diretas aos veículos quando necessário.

---

Para mais informações detalhadas sobre cada serviço e sua configuração, consulte a documentação específica em suas respectivas pastas.
