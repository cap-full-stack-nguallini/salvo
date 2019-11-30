package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
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
                                    GamePlayerRepository  gamePlayerRepository,
                                    ShipRepository  shipRepository,
                                    SalvoRepository salvoRepository) {
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

      String battleship = "Battleship";
      String submarine = "Submarine";
      String destroyer = "Destroyer";
      String patrolBoat = "Patrol Boat";
      Ship ship1 = new Ship(destroyer, Arrays.asList("H2", "H3", "H4"),gamePlayer1);
      Ship ship2 = new Ship(submarine, Arrays.asList("E1", "F1", "G1"),gamePlayer1);
      Ship ship3 = new Ship(patrolBoat, Arrays.asList("B4", "B5"),gamePlayer1);
      Ship ship4 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"),gamePlayer2);
      Ship ship5 = new Ship(patrolBoat, Arrays.asList("F1", "F2"),gamePlayer2);

      shipRepository.save(ship1);
      shipRepository.save(ship2);
      shipRepository.save(ship3);
      shipRepository.save(ship4);
      shipRepository.save(ship5);

      Salvo salvo1 = new Salvo(1,Arrays.asList("H2", "H3", "H4"),gamePlayer1);
      Salvo salvo2 = new Salvo(1,Arrays.asList("E1", "F1", "G1"),gamePlayer2);

      salvoRepository.save(salvo1);
      salvoRepository.save(salvo2);

    };
  }
}
