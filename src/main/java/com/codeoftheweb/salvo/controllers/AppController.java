package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.util.GameState;
import com.codeoftheweb.salvo.util.Util;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    if(Util.isGuest(authentication)){
      return new  ResponseEntity<>(Util.makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    Player  playerLogued  = playerRepository.findByEmail(authentication.getName()).orElse(null);
    GamePlayer gamePlayer = gamePlayerRepository.findById(nn).orElse(null);

    if(playerLogued ==  null){
      return new  ResponseEntity<>(Util.makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer ==  null ){
      return new  ResponseEntity<>(Util.makeMap("error","Paso algo"),HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer.getPlayer().getId() !=  playerLogued.getId()){
      return new  ResponseEntity<>(Util.makeMap("error","Paso algo"),HttpStatus.CONFLICT);
    }

	  Map<String,  Object>  dto = new LinkedHashMap<>();
	  Map<String, Object> hits = new LinkedHashMap<>();

	    hits.put("self", gethits(gamePlayer, gamePlayer.getOpponent()));
		  hits.put("opponent", gethits(gamePlayer.getOpponent(),  gamePlayer));

	    dto.put("id", gamePlayer.getGame().getId());
      dto.put("created",  gamePlayer.getGame().getCreated());
	    dto.put("gameState", getGameState(gamePlayer));

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
  public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameID, Authentication authentication) {
    if (Util.isGuest(authentication)){
      return new ResponseEntity<>(Util.makeMap("error", "You can't join a Game if You're Not Logged In!"), HttpStatus.UNAUTHORIZED);
    }

    Player  player  = playerRepository.findByEmail(authentication.getName()).orElse(null);
    Game gameToJoin = gameRepository.getOne(gameID);

    if (gameRepository.getOne(gameID) == null) {
      return new ResponseEntity<>(Util.makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
    }

    if(player ==  null){
      return new ResponseEntity<>(Util.makeMap("error", "No such game."), HttpStatus.FORBIDDEN);
    }

    long gamePlayersCount = gameToJoin.getGamePlayers().size();

    if (gamePlayersCount == 1) {
      GamePlayer gameplayer = gamePlayerRepository.save(new GamePlayer(gameToJoin, player));
      return new ResponseEntity<>(Util.makeMap("gpid", gameplayer.getId()), HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(Util.makeMap("error", "Game is full!"), HttpStatus.FORBIDDEN);
    }
  }

  private List<Map> gethits(GamePlayer  self, GamePlayer  opponent){

    List<Map> hits  = new ArrayList<>();

    Integer carrierDamage = 0;
    Integer battleshipDamage = 0;
    Integer submarineDamage = 0;
    Integer destroyerDamage = 0;
    Integer patrolboatDamage = 0;

    List <String> carrierLocation = getLocatiosByType("carrier",self);
    List <String> battleshipLocation = getLocatiosByType("battleship",self);
    List <String> submarineLocation = getLocatiosByType("submarine",self);
    List <String> destroyerLocation = getLocatiosByType("destroyer",self);
    List <String> patrolboatLocation = getLocatiosByType("patrolboat",self);

    for (Salvo  salvo : opponent.getSalvoes()){

      long carrierHitsInTurn = 0;
      long battleshipHitsInTurn = 0;
      long submarineHitsInTurn = 0;
      long destroyerHitsInTurn = 0;
      long patrolboatHitsInTurn = 0;
      long missedShots = salvo.getSalvoLocations().size();

      Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
      Map<String, Object> damagesPerTurn = new LinkedHashMap<>();

      List<String> salvoLocationsList = new ArrayList<>();
      List<String> hitCellsList = new ArrayList<>();

      for (String salvoShot : salvo.getSalvoLocations()) {
        if (carrierLocation.contains(salvoShot)) {
          carrierDamage++;
          carrierHitsInTurn++;
          hitCellsList.add(salvoShot);
          missedShots--;
        }
        if (battleshipLocation.contains(salvoShot)) {
          battleshipDamage++;
          battleshipHitsInTurn++;
          hitCellsList.add(salvoShot);
          missedShots--;
        }
        if (submarineLocation.contains(salvoShot)) {
          submarineDamage++;
          submarineHitsInTurn++;
          hitCellsList.add(salvoShot);
          missedShots--;
        }
        if (destroyerLocation.contains(salvoShot)) {
          destroyerDamage++;
          destroyerHitsInTurn++;
          hitCellsList.add(salvoShot);
          missedShots--;
        }
        if (patrolboatLocation.contains(salvoShot)) {
          patrolboatDamage++;
          patrolboatHitsInTurn++;
          hitCellsList.add(salvoShot);
          missedShots--;
        }
      }

      damagesPerTurn.put("carrierHits", carrierHitsInTurn);
      damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
      damagesPerTurn.put("submarineHits", submarineHitsInTurn);
      damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
      damagesPerTurn.put("patrolboatHits", patrolboatHitsInTurn);
      damagesPerTurn.put("carrier", carrierDamage);
      damagesPerTurn.put("battleship", battleshipDamage);
      damagesPerTurn.put("submarine", submarineDamage);
      damagesPerTurn.put("destroyer", destroyerDamage);
      damagesPerTurn.put("patrolboat", patrolboatDamage);

      hitsMapPerTurn.put("turn", salvo.getTurn());
      hitsMapPerTurn.put("hitLocations", hitCellsList);
      hitsMapPerTurn.put("damages", damagesPerTurn);
      hitsMapPerTurn.put("missed", missedShots);
      hits.add(hitsMapPerTurn);

    };

    return hits;
  }

  private GameState getGameState (GamePlayer gamePlayer) {

    if (gamePlayer.getShips().size() == 0) {
      return GameState.PLACESHIPS;
    }
    if (gamePlayer.getGame().getGamePlayers().size() == 1){
      return GameState.WAITINGFOROPP;
    }
    if (gamePlayer.getGame().getGamePlayers().size() == 2) {

      GamePlayer opponentGp = gamePlayer.getOpponent();

      if ((gamePlayer.getSalvoes().size() == opponentGp.getSalvoes().size()) && (getIfAllSunk(opponentGp, gamePlayer)) && (!getIfAllSunk(gamePlayer, opponentGp))) {
        return GameState.WON;
      }
      if ((gamePlayer.getSalvoes().size() == opponentGp.getSalvoes().size()) && (getIfAllSunk(opponentGp, gamePlayer)) && (getIfAllSunk(gamePlayer, opponentGp))) {
        return GameState.TIE;
      }
      if ((gamePlayer.getSalvoes().size() == opponentGp.getSalvoes().size()) && (!getIfAllSunk(opponentGp, gamePlayer)) && (getIfAllSunk(gamePlayer, opponentGp))) {
        return GameState.LOST;
      }

      if ((gamePlayer.getSalvoes().size() == opponentGp.getSalvoes().size()) && (gamePlayer.getId() < opponentGp.getId())) {
        return GameState.PLAY;
      }
      if (gamePlayer.getSalvoes().size() < opponentGp.getSalvoes().size()){
        return GameState.PLAY;
      }
      if ((gamePlayer.getSalvoes().size() == opponentGp.getSalvoes().size()) && (gamePlayer.getId() > opponentGp.getId())) {
        return GameState.WAIT;
      }
      if (gamePlayer.getSalvoes().size() > opponentGp.getSalvoes().size()){
        return GameState.WAIT;
      }

    }
    return GameState.UNDEFINED;
  }

  private List<String>  getLocatiosByType(String type, GamePlayer self){
    return  self.getShips().size()  ==  0 ? new ArrayList<>() : self.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst().get().getShipLocations();
  }

  private Boolean getIfAllSunk (GamePlayer self, GamePlayer opponent) {

  	if(!opponent.getShips().isEmpty() && !self.getSalvoes().isEmpty()){
		  return opponent.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList()).containsAll(self.getShips().stream()
						                                .flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList()));
	  }
			return false;
  }

  public Map makeMap(String key, Object  value){
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }



}
