package br.edu.infnet.micro.servicolocalizacao.service;

import br.edu.infnet.micro.servicolocalizacao.model.Rastreio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    private final Map<String, String> macToQueueMap = new HashMap<>();

    public LocationService(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.objectMapper = objectMapper;
    }

    public void processLocationData(String data) {
        try {
            Rastreio rastreio = parseRastreio(data);
            String jsonRastreio = objectMapper.writeValueAsString(rastreio);
            String queueName = getOrCreateQueue(rastreio.getMacAddress());
            sendToRabbitMQ(jsonRastreio, queueName);
            logger.info("Rastreio processado e enviado para a fila: {}", queueName);
        } catch (Exception e) {
            logger.error("Erro ao processar dados de rastreio", e);
        }
    }

    private String getOrCreateQueue(String macAddress) {
        return macToQueueMap.computeIfAbsent(macAddress, this::createQueue);
    }

    private String createQueue(String macAddress) {
        String queueName = "queue." + macAddress;
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-expires", 300000); // 5 minutos em milissegundos
        arguments.put("x-message-ttl", 300000); // 5 minutos em milissegundos
        Queue queue = new Queue(queueName, true, false, false, arguments);
        rabbitAdmin.declareQueue(queue);
        logger.info("Fila criada para o MAC address: {} com expiração e TTL de 5 minutos", macAddress);
        return queueName;
    }

    private Rastreio parseRastreio(String data) {
        String[] parts = data.split(",");
        if (parts.length >= 8) {
            return Rastreio.builder()
                .macAddress(parts[0])
                .latitude(Double.parseDouble(parts[1]))
                .longitude(Double.parseDouble(parts[2]))
                .altitude(Double.parseDouble(parts[3]))
                .velocidade(Double.parseDouble(parts[4]))
                .direcao(parts[5])
                .statusVeiculo(parts[6])
                .timestamp(Long.parseLong(parts[7]))
                .build();
        } else {
            throw new IllegalArgumentException("Formato de dados inválido");
        }
    }

    private void sendToRabbitMQ(String message, String queueName) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            logger.info("Mensagem enviada para a fila: {}", queueName);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ", e);
        }
    }
}
