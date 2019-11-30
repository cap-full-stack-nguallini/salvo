package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

  @RequestMapping("/games")
  public List<Map<String,Object>> getGameAll(){
    return gameRepository.findAll()
            .stream()
            .map(game -> game.makeGameDTO())
            .collect(Collectors.toList());
  }

  @RequestMapping("/game_view/{nn}")
  public Map<String,  Object> getGameViewByGamePlayerID(@PathVariable Long nn) {
    GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();

    Map<String,  Object>  dto = new LinkedHashMap<>();
      dto.put("id", gamePlayer.getGame().getId());
      dto.put("created",  gamePlayer.getGame().getCreationDate());
      dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                                                  .stream()
                                                  .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                                                  .collect(Collectors.toList()));
      dto.put("ships",  gamePlayer.getShips()
                                  .stream()
                                  .map(ship -> ship.makeShipDTO())
                                  .collect(Collectors.toList()));

    return  dto;
  }




}
