package br.edu.infnet.micro.consumidor_caminhao.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${caminhao.mac-address}")
    private String macAddress;

    @Value("${caminhao.placa}")
    private String placa;

    @Bean
    public String inputQueueName() {
        return "queue." + macAddress;
    }

    @Bean
    public String outputQueueName() {
        return placa;
    }

    @Bean
    public Queue inputQueue() {
        return new Queue(inputQueueName(), false);
    }

    @Bean
    public Queue outputQueue() {
        return new Queue(outputQueueName(), true);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
