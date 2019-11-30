package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

    @Bean
    public CommandLineRunner initData(PlayerRepository playereRepository) {
      return (args) -> {

        Player player1 = new Player("davidfc05@gmail.com");
        Player player2 = new Player("rocket@gmail.com");

        playereRepository.save(player1);
        playereRepository.save(player2);
      };
	}
}
