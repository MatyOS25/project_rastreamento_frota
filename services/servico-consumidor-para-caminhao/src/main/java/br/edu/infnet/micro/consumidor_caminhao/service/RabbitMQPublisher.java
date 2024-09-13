package br.edu.infnet.micro.consumidor_caminhao.service;

import br.edu.infnet.micro.consumidor_caminhao.model.Caminhao;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public void publicarMensagem(Caminhao caminhao, String mensagem) {
        // Usar a placa como routing key
        String routingKey = caminhao.getPlaca();

        rabbitTemplate.convertAndSend(exchangeName, routingKey, mensagem);
    }
}
