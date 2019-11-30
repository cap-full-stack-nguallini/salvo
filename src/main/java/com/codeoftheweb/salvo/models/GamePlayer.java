
package com.codeoftheweb.salvo.models;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public  class   GamePlayer{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native",  strategy = "native")
    private long id;

    private Date  joinDate;

    @ManyToOne(fetch  = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game  game;
    
    @ManyToOne(fetch  = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player  player;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private List<Ship> ships;

    public  GamePlayer(){
      this.joinDate = new Date();
    }

    public  GamePlayer(Game game, Player player){
    this.joinDate = new  Date();
    this.game = game;
    this.player = player;
  }

  public Map<String,  Object> makeGamePlayerDTO(){
    Map<String,  Object>  dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("player", this.getPlayer().makePlayerDTO());
    return  dto;
  }

  public long getId() {
    return id;
  }

  public Date getJoinDate() {
    return joinDate;
  }

  public  void  setGame(Game  game){
      this.game = game;
    }

    public  void  setPlayer(Player  player){
      this.player = player;
    }

    public  void  setJoinDate(Date joinDate){
      this.joinDate = joinDate;
    }

    public  Game  getGame(){
      return  this.game;
    }

    public  Player  getPlayer(){
      return  this.player;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }
}