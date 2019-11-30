package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
  GameRepository  gameRepository;

  @Autowired
  GamePlayerRepository  gamePlayerRepository;

  @RequestMapping("/games")
  public List<Map<String,Object>> getGameAll(){
    return gameRepository.findAll()
            .stream()
            .map(game -> game.makeGameDTO())
            .collect(Collectors.toList());
  }
}
