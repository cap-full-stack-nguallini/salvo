
package com.codeoftheweb.salvo.models;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.*;

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
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private List<Salvo> salvoes;

    public  GamePlayer(){

    	this.joinDate = new Date();
    	this.ships  = new HashSet<>();
      this.salvoes  = new ArrayList<>();
    }

    public  GamePlayer(Game game, Player player){
    this.joinDate = new  Date();
    this.game = game;
    this.player = player;
    this.ships  = new HashSet<>();
    this.salvoes  = new ArrayList<>();
  }

  public Map<String,  Object> makeGamePlayerDTO(){
    Map<String,  Object>  dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("player", this.getPlayer().makePlayerDTO());
    return  dto;
  }

  public GamePlayer getOpponent(){
    	return  this.getGame().getGamePlayers().stream()
					    .filter(gamePlayer -> gamePlayer.getId()  !=  this.getId())
					    .findFirst()
					    .orElse(new GamePlayer());
  }

  public Optional<Score>  getScore(){
      return  this.getPlayer().getScore(this.getGame());
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

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

	public List<Salvo> getSalvoes() {
		return salvoes;
	}

	public void setSalvoes(List<Salvo> salvoes) {
		this.salvoes = salvoes;
	}
}