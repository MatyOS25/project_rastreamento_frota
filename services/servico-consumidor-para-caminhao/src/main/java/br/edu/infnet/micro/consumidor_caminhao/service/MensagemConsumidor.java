package br.edu.infnet.micro.consumidor_caminhao.service;

import br.edu.infnet.micro.consumidor_caminhao.model.Caminhao;
import br.edu.infnet.micro.consumidor_caminhao.model.Localizacao;
import br.edu.infnet.micro.consumidor_caminhao.model.LocalizacaoCassandra;
import br.edu.infnet.micro.consumidor_caminhao.repository.LocalizacaoCassandraRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;

import javax.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MensagemConsumidor implements ChannelAwareMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(MensagemConsumidor.class);

	@Autowired
	private RabbitMQPublisher rabbitMQPublisher;

	@Autowired
    private LocalizacaoCassandraRepository cassandraRepository;

	@Autowired
	private SimpleMessageListenerContainer messageListenerContainer;

	@Autowired
    private ObjectMapper objectMapper;

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
		messageListenerContainer.setMessageListener(this);
	}

	@Override
	public void onMessage(Message message, com.rabbitmq.client.Channel channel) {
		String routingKey = message.getMessageProperties().getReceivedRoutingKey();
		String jsonPayload = new String(message.getBody(), StandardCharsets.UTF_8);
		logger.info("Mensagem recebida para o caminhão com MAC {}: {}", macAddress, jsonPayload);
		
		processarMensagem(jsonPayload);
		
		rabbitMQPublisher.publicarMensagem(caminhao, jsonPayload);
		logger.info("Mensagem processada enviada para a fila caminhao.{}", placa);
	}

	private void processarMensagem(String jsonPayload) {
		try {
			logger.info("Payload original recebido: {}", jsonPayload);

			// Remova as aspas extras no início e no fim do payload
			jsonPayload = jsonPayload.replaceAll("^\"|\"$", "");
			
			// Remova as barras invertidas extras
			jsonPayload = jsonPayload.replace("\\", "");
			
			logger.info("Payload processado: {}", jsonPayload);
			
			Localizacao localizacao = objectMapper.readValue(jsonPayload, Localizacao.class);
			logger.info("Localizacao desserializada: {}", localizacao);
			
			LocalizacaoCassandra localizacaoCassandra = LocalizacaoCassandra.builder()
				.placa(placa)
				.timestamp(Instant.ofEpochSecond(localizacao.getTimestamp()))
				.macAddress(localizacao.getMacAddress())
				.latitude(localizacao.getLatitude())
				.longitude(localizacao.getLongitude())
				.altitude(localizacao.getAltitude())
				.velocidade(localizacao.getVelocidade())
				.direcao(localizacao.getDirecao())
				.statusVeiculo(localizacao.getStatusVeiculo())
				.build();

			cassandraRepository.save(localizacaoCassandra);
			logger.info("Localização salva no Cassandra: {}", localizacaoCassandra);
		} catch (IOException e) {
			logger.error("Erro ao desserializar mensagem JSON: ", e);
			logger.error("Payload que causou o erro: {}", jsonPayload);
		} catch (Exception e) {
			logger.error("Erro ao salvar no Cassandra: ", e);
			logger.error("Detalhes do erro: ", e);
		}
	}
}
