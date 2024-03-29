package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayerController {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private PlayereRepository playerRepository;

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> addPlayer(
          @RequestParam String email, @RequestParam String password) {

    if (email.isEmpty() || password.isEmpty()) {
      return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
    }

    if (playerRepository.findByEmail(email).orElse(null) !=  null) {
      return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
    }

    playerRepository.save(new Player(email, passwordEncoder.encode(password)));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
