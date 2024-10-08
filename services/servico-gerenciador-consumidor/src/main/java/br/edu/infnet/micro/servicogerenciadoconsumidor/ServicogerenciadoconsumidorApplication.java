package br.edu.infnet.micro.servicogerenciadoconsumidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
@EnableDiscoveryClient
public class ServicogerenciadoconsumidorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicogerenciadoconsumidorApplication.class, args);
	}

}
