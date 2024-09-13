package br.edu.infnet.micro.servicolocalizacao.service;

import br.edu.infnet.micro.servicolocalizacao.model.Rastreio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public LocationService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void processLocationData(String data) {
        try {
            Rastreio rastreio = parseRastreio(data);
            String jsonRastreio = objectMapper.writeValueAsString(rastreio);
            sendToRabbitMQ(jsonRastreio);
            logger.info("Rastreio processado e enviado para a fila: {}", rastreio.getMacAddress());
        } catch (Exception e) {
            logger.error("Erro ao processar dados de rastreio", e);
        }
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

    private void sendToRabbitMQ(String message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            logger.info("Mensagem enviada para RabbitMQ: {}", message);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para RabbitMQ", e);
        }
    }
}
