package br.edu.infnet.micro.consumidor_caminhao.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
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
        return "caminhao." + placa;
    }

    @Bean
    public Queue outputQueue() {
        return new Queue(outputQueueName(), true, false, false);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(inputQueueName());
        container.setMissingQueuesFatal(false);
        return container;
    }
}
