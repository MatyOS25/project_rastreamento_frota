package br.edu.infnet.micro.consumidor_caminhao.service;

import br.edu.infnet.micro.consumidor_caminhao.model.Caminhao;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MensagemConsumidor {

	@Autowired
	private RabbitMQPublisher rabbitMQPublisher;

	@Value("${caminhao.mac-address}")
	private String macAddress;

	@Value("${caminhao.placa}")
	private String placa;

	@Value("${caminhao.ativo}")
	private boolean ativo;

	@Value("${caminhao.motorista}")
	private String motorista;

	@Value("${caminhao.carreta}")
	private String carreta;

	private Caminhao caminhao;

	@PostConstruct
	public void init() {
		caminhao = new Caminhao(null, macAddress, placa, ativo, motorista, carreta);
	}

	@RabbitListener(queues = "#{inputQueueName}")
	public void receberMensagem(Message message) {
		String mensagem = new String(message.getBody());
		System.out.println("Mensagem recebida para o caminhão com MAC " + macAddress + ": " + mensagem);
		
		// Processar a mensagem (você pode adicionar lógica adicional aqui)
		
		// Publicar a mensagem processada na fila de saída (baseada na placa)
		rabbitMQPublisher.publicarMensagem(caminhao, "Mensagem processada: " + mensagem);
		System.out.println("Mensagem processada enviada para a fila " + placa);
	}
}
