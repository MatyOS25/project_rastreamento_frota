package br.edu.infnet.micro.consumidor_caminhao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsumidorCaminhaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumidorCaminhaoApplication.class, args);
	}

}
