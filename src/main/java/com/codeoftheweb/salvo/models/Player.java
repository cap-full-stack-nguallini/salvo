package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native",  strategy = "native")
    private long id;

    private String email;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Player(){}

    public Player(String userName){
        this.email =   userName;
    }

    public Map<String,  Object> makePlayerDTO(){
        Map<String,  Object>    dto=    new LinkedHashMap<>();
        dto.put("id",   this.getId());
        dto.put("email", this.getEmail());
        return  dto;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}
