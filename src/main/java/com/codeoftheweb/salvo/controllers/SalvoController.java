package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.util.Util;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import com.codeoftheweb.salvo.repositories.SalvoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	PlayereRepository playerRepository;

	@Autowired
	GamePlayerRepository  gamePlayerRepository;

	@Autowired
	SalvoRepository salvoRepository;

	@RequestMapping(value = "/games/players/{gpid}/salvoes",  method = RequestMethod.POST)
	public ResponseEntity<Map>  addSalvo(@PathVariable long gpid, @RequestBody Salvo  salvo, Authentication authentication){

		if(isGuest(authentication)){
			return new ResponseEntity<>(Util.makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
		}

		Player playerLogued  = playerRepository.findByEmail(authentication.getName()).orElse(null);
		GamePlayer self  = gamePlayerRepository.getOne(gpid);

		if(playerLogued ==  null){
			return new ResponseEntity<>(Util.makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
		}

		if(self == null){
			return new ResponseEntity<>(Util.makeMap("error","NO esta autorizado"), HttpStatus.UNAUTHORIZED);
		}

		if(self.getPlayer().getId() !=  playerLogued.getId()){
			return new ResponseEntity<>(Util.makeMap("error","Los players no coinciden"), HttpStatus.FORBIDDEN);
		}

		GamePlayer  opponent  = self.getOpponent();

		if(opponent.getId() == 0){
			return new ResponseEntity<>(Util.makeMap("Error","NO hay Oponente no puede disparar"),HttpStatus.CONFLICT);
		}

		if(self.getSalvoes().size() <=  opponent.getSalvoes().size()){
			salvo.setTurn(self.getSalvoes().size()  + 1);
			salvo.setGamePlayer(self);
			salvoRepository.save(salvo);
			return  new ResponseEntity<>(Util.makeMap("OK","Salvo created!!"), HttpStatus.CREATED);
		}

		return  new ResponseEntity<>(Util.makeMap("Error","Ya jugaste"), HttpStatus.CREATED);
	}

	private boolean isGuest(Authentication authentication) {
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}
}
