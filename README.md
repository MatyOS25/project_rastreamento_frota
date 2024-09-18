# Projeto de Rastreamento de Frota

## Visão Geral

Este projeto implementa um sistema de rastreamento de frota utilizando uma arquitetura de microserviços. O sistema recebe dados de localização dos veículos via UDP e processa essas informações para fornecer insights sobre a frota.

## Arquitetura

O sistema é composto pelos seguintes serviços principais:

1. **servico-localizacao**: Recebe pacotes UDP dos veículos e os envia para uma fila RabbitMQ.
2. **servico-consumidor-para-caminhao**: Consome mensagens da fila, processa os dados e os armazena no Cassandra.
3. **servico-gerenciador-consumidor**: Gerencia os consumidores e os caminhões, interagindo com a API do Kubernetes.

### Fluxo de Dados

1. Veículos enviam pacotes UDP com informações de localização.
2. `servico-localizacao` recebe os pacotes e os envia para o RabbitMQ.
3. `servico-consumidor-para-caminhao` consome as mensagens, processa os dados e os salva no Cassandra.
4. `servico-gerenciador-consumidor` gerencia o ciclo de vida dos consumidores e dos caminhões.

## Dados Processados

- MAC Address
- Latitude
- Longitude
- Altitude
- Velocidade
- Direção
- Status do veículo
- Timestamp

## Tecnologias Utilizadas

- UDP para comunicação com veículos
- RabbitMQ para filas de mensagens
- Cassandra para armazenamento de dados de localização
- MongoDB para armazenamento de dados dos caminhões
- Kubernetes para orquestração de containers
- Helm para gerenciamento de pacotes Kubernetes
- Spring Boot para desenvolvimento dos microserviços
- Eureka para service discovery
- Papertrail para logging centralizado
- OpenTelemetry para instrumentação

## Processo de Implantação

### Pré-requisitos

- Docker Desktop com Kubernetes habilitado (para desenvolvimento local)
- Kubernetes CLI (kubectl)
- Helm

### Passos para Implantação Local

1. Certifique-se de que o Docker Desktop está rodando com Kubernetes habilitado.
2. Clone o repositório do projeto.
3. Possuir kubernetes-helm (choco install kubernetes-helm)
4. Na pasta scripts rode o build_and_dockerize.ps1 passando o nome de cada servico, rodar um por vez:
      api-gateway, eureka-server, servico-consumidor-para-caminhao, servico-gerenciador-consumidor, servico-localizacao
5. Rodar o script que esta na pasta principal: deploy_with_helm.ps1
6. Verifique se os pods estão rodando:
   ```bash
   kubectl get pods
   ```

### Implantação em Ambiente de Produção

Para ambientes de produção, ajuste os valores nos arquivos `values.yaml` de cada chart e siga um processo similar ao da implantação local, mas apontando para o cluster Kubernetes de produção.

## Serviços

### Serviço de Localização

Recebe dados UDP e os envia para o RabbitMQ. Principais funcionalidades:

- Parsing de dados UDP
- Envio de mensagens para o RabbitMQ
- Criação de filas duráveis para cada caminhão

### Serviço Consumidor para Caminhão

Consome mensagens do RabbitMQ, processa e salva no Cassandra. Principais funcionalidades:

- Desserialização de mensagens JSON
- Processamento de dados de localização
- Armazenamento de dados no Cassandra
- Publicação de mensagens processadas de volta no RabbitMQ

### Serviço Gerenciador de Consumidores

Gerencia o ciclo de vida dos consumidores e dos caminhões. Principais funcionalidades:

- CRUD de caminhões
- Criação, atualização e remoção de pods Kubernetes para consumidores
- Verificação periódica do estado dos consumidores

## Configuração

Cada serviço possui seu próprio arquivo `application.properties` e `values.yaml` (para Helm) com configurações específicas. Certifique-se de configurar corretamente:

- Conexões com RabbitMQ
- Conexões com Cassandra
- Conexões com MongoDB
- Configurações do Kubernetes
- Logging e monitoramento

## Monitoramento e Logging

- Utiliza Papertrail para logging centralizado
- OpenTelemetry para instrumentação e coleta de métricas

## Desenvolvimento

Para desenvolver e testar localmente:

1. Configure o Docker Desktop com Kubernetes habilitado.
2. Use o script `TestUDP-servico-localizacao.ps1` para simular o envio de dados UDP.
3. Implante os serviços usando Helm conforme descrito na seção de Implantação.
4. Para desenvolvimento rápido, você pode executar serviços individuais diretamente via Spring Boot, conectando-se aos serviços implantados no Kubernetes local.

