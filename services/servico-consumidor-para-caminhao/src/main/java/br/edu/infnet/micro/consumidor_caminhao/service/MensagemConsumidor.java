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

	@Autowired
	private FilaManager filaManager;

	@Value("${caminhao.mac-address:00:00:00:00:00:00}")
	private String macAddress;

	@Value("${caminhao.placa:ABC1234}")
	private String placa;

	@Value("${caminhao.ativo:true}")
	private boolean ativo;

	@Value("${caminhao.motorista:Motorista Padrão}")
	private String motorista;

	@Value("${caminhao.carreta:Carreta Padrão}")
	private String carreta;

	private Caminhao caminhao;

	@PostConstruct
	public void init() {
		caminhao = new Caminhao(null, macAddress, placa, ativo, motorista, carreta);
		filaManager.criarOuAtualizarFila(caminhao);
	}

	@RabbitListener(queues = "${rabbitmq.queue.name}")
	public void receberMensagem(Message message) {
		String mensagem = new String(message.getBody());
		System.out.println("Mensagem recebida para o caminhão com placa " + placa + ": " + mensagem);
		
		// Processar a mensagem (você pode adicionar lógica adicional aqui)
		
		// Publicar a mensagem processada no tópico específico do caminhão
		rabbitMQPublisher.publicarMensagem(caminhao, "Mensagem processada: " + mensagem);
	}
}
