package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public CommandLineRunner initData(PlayereRepository playereRepository,
                                    GameRepository gameRepository,
                                    GamePlayerRepository  gamePlayerRepository) {
    return (args) -> {

      Player playe1  = new Player("david@gmail.com");
      Player  playe2  = new Player("rocket@gmail.com");

      playereRepository.save(playe1);
      playereRepository.save(playe2);

      Game game1 = new Game();
      Game game2 = new Game();
      game2.setCreationDate(Date.from(game1.getCreationDate().toInstant().plusSeconds(3600)));
      Game game3 = new Game();
      game3.setCreationDate(Date.from(game1.getCreationDate().toInstant().plusSeconds(7200)));

      gameRepository.save(game1);
      gameRepository.save(game2);
      gameRepository.save(game3);

      GamePlayer  gamePlayer1 = new GamePlayer(game1,playe1);
      GamePlayer  gamePlayer2 = new GamePlayer(game1,playe2);

      gamePlayerRepository.save(gamePlayer1);
      gamePlayerRepository.save(gamePlayer2);

    };
  }
}
