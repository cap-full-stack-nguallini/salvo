package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ShipController {

	@Autowired
	private PlayereRepository playerRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@Autowired
	private ShipRepository  shipRepository;

	@RequestMapping(path = "/games/players/{gpid}/ships",  method = RequestMethod.POST)
	public ResponseEntity<Map>  addShip(@PathVariable long gpid, @RequestBody Set<Ship> ships, Authentication authentication){

			if(isGuest(authentication)){
				return new ResponseEntity<>(makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
			}

			Player  player  = playerRepository.findByEmail(authentication.getName()).orElse(null);
			GamePlayer  gamePlayer  = gamePlayerRepository.getOne(gpid);

			if(player ==  null){
				return new ResponseEntity<>(makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
			}

			if(gamePlayer == null){
				return new ResponseEntity<>(makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
			}

			if(gamePlayer.getPlayer().getId() !=  player.getId()){
				return new ResponseEntity<>(makeMap("error","Los players no coinciden"), HttpStatus.FORBIDDEN);
			}

			if(!gamePlayer.getShips().isEmpty()){
				return new ResponseEntity<>(makeMap("error","NO esta autorizado ya tengo ships"), HttpStatus.UNAUTHORIZED);
			}

			ships.forEach(ship -> {
				ship.setGamePlayer(gamePlayer);
				shipRepository.save(ship);
			});

			return new ResponseEntity<>(makeMap("OK","Ship created"), HttpStatus.CREATED);
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
