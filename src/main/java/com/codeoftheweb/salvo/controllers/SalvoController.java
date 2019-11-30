package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

  @Autowired
  private GameRepository  gameRepository;

  @Autowired
  private GamePlayerRepository  gamePlayerRepository;

  @Autowired
  private PlayereRepository playerRepository;

  @RequestMapping("/games")
  public Map<String,Object> getGameAll(Authentication authentication){
    Map<String,  Object>  dto = new LinkedHashMap<>();

    if(isGuest(authentication)){
      dto.put("player", "Guest");
    }else{
      Player player  = playerRepository.findByEmail(authentication.getName()).get();
      dto.put("player", player.makePlayerDTO());
    }

    dto.put("games", gameRepository.findAll()
                                  .stream()
                                  .map(game -> game.makeGameDTO())
                                  .collect(Collectors.toList()));
    return dto;
  }

  @RequestMapping("/game_view/{nn}")
  public Map<String,  Object> getGameViewByGamePlayerID(@PathVariable Long nn) {
    GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();

    Map<String,  Object>  dto = new LinkedHashMap<>();
      dto.put("id", gamePlayer.getGame().getId());
      dto.put("created",  gamePlayer.getGame().getCreated());
      dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                                                  .stream()
                                                  .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                                                  .collect(Collectors.toList()));
      dto.put("ships",  gamePlayer.getShips()
                                  .stream()
                                  .map(ship -> ship.makeShipDTO())
                                  .collect(Collectors.toList()));
      dto.put("salvoes",  gamePlayer.getGame().getGamePlayers()
                                              .stream()
                                              .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                                                                                 .stream()
                                                                                 .map(salvo -> salvo.makeSalvoDTO()))
                                              .collect(Collectors.toList()));

    return  dto;
  }

  @RequestMapping("/leaderBoard")
  public  List<Map<String,Object>> leaderBoard(){

    return  playerRepository.findAll()
                            .stream()
                            .map(player  ->  player.makePlayerScoreDTO())
                            .collect(Collectors.toList());


  }

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

}
