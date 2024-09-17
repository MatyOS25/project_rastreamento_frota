package br.edu.infnet.micro.consumidor_caminhao.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitMQInitializer {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private Queue outputQueue;

    @PostConstruct
    public void initialize() {
        amqpAdmin.declareQueue(outputQueue);
    }
}
