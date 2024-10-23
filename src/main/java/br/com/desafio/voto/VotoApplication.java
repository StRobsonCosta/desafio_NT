package br.com.desafio.voto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafka
@Slf4j
public class VotoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotoApplication.class, args);
		log.info("Aplicação iniciada com sucesso!");
	}

}
