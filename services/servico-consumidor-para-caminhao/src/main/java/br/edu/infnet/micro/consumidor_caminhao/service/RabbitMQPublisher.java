package br.edu.infnet.micro.consumidor_caminhao.service;

import br.edu.infnet.micro.consumidor_caminhao.model.Caminhao;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private String outputQueueName;

    public void publicarMensagem(Caminhao caminhao, String mensagem) {
        rabbitTemplate.convertAndSend(outputQueueName, mensagem);
    }
}
