package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired
  private GameRepository  gameRepository;

  @Autowired
  private GamePlayerRepository  gamePlayerRepository;

  @Autowired
  private PlayereRepository playerRepository;

  @Autowired
  private ShipRepository  shipRepository;

  @RequestMapping("/game_view/{nn}")
  public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long nn, Authentication  authentication) {

    if(isGuest(authentication)){
      return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    Player  player  = playerRepository.findByEmail(authentication.getName()).orElse(null);
    GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);

    if(player ==  null){
      return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer ==  null ){
      return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer.getPlayer().getId() !=  player.getId()){
      return new  ResponseEntity<>(makeMap("error","Paso algo"),HttpStatus.CONFLICT);
    }

	  Map<String,  Object>  dto = new LinkedHashMap<>();
	  Map<String, Object> hits = new LinkedHashMap<>();
		  hits.put("self", new ArrayList<>());
		  hits.put("opponent", new ArrayList<>());

	    dto.put("id", gamePlayer.getGame().getId());
      dto.put("created",  gamePlayer.getGame().getCreated());
	    dto.put("gameState", "PLACESHIPS");

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
	    dto.put("hits", hits);


	  return  new ResponseEntity<>(dto,HttpStatus.OK);
  }

  @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {
    if (isGuest(authentication)){
      return new ResponseEntity<>(makeMap("error", "You can't join a Game if You're Not Logged In!"), HttpStatus.UNAUTHORIZED);
    }

    Player  player  = playerRepository.findByEmail(authentication.getName()).orElse(null);
    Game gameToJoin = gameRepository.getOne(gameID);

    // assert (gameToJoin != null);

    if (gameRepository.getOne(gameID) == null) {
      return new ResponseEntity<>(makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
    }

    if(player ==  null){
      return new ResponseEntity<>(makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
    }

    long gamePlayersCount = gameToJoin.getGamePlayers().size();

    if (gamePlayersCount == 1) {
      GamePlayer gameplayer = gamePlayerRepository.save(new GamePlayer(gameToJoin, player));
      return new ResponseEntity<>(makeMap("gpid", gameplayer.getId()), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(makeMap("error", "Game is full!"), HttpStatus.FORBIDDEN);
    }
  }





  @RequestMapping("/leaderBoard")
  public  List<Map<String,Object>> leaderBoard(){

    return  playerRepository.findAll()
                            .stream()
                            .map(player  ->  player.makePlayerScoreDTO())
                            .collect(Collectors.toList());


  }

  @RequestMapping(value = "/games/players/{gpid}/ships")
  public ResponseEntity<Map>  addShips(@PathVariable long gpid, @RequestBody  List<Ship>  ships,  Authentication  authentication){

   if(isGuest(authentication)){
     return new ResponseEntity<>(makeMap("error","Is guest"),HttpStatus.UNAUTHORIZED);
   }

   Player player  = playerRepository.findByEmail(authentication.getName()).get();

   GamePlayer gamePlayer  = gamePlayerRepository.getOne(gpid);

   if(gamePlayer  ==  null){
     return new ResponseEntity<>(makeMap("error","Is guest"),HttpStatus.UNAUTHORIZED);
   }

   if(gamePlayer.getPlayer().getId()  !=  player.getId()){
     return new ResponseEntity<>(makeMap("error","Is guest"),HttpStatus.UNAUTHORIZED);
   }

   if(!gamePlayer.getShips().isEmpty()){
     return new ResponseEntity<>(makeMap("error","ya tienes barcos"),HttpStatus.UNAUTHORIZED);
   }

   ships.forEach(ship -> {
     ship.setGamePlayer(gamePlayer);
     shipRepository.save(ship);
   });

   return new ResponseEntity<>(makeMap("OK","ships created"),HttpStatus.CREATED);
  }

  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }

  private Map<String, Object> makeMap(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }




}
