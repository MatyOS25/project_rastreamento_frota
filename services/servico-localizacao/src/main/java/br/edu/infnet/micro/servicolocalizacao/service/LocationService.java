package br.edu.infnet.micro.servicolocalizacao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public LocationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void processLocationData(String data) {
        logger.info("Processando dados de localização: {}", data);
        sendToRabbitMQ(data);
    }

    private void sendToRabbitMQ(String message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            logger.info("Mensagem enviada para RabbitMQ: {}", message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ", e);
        }
    }
}
