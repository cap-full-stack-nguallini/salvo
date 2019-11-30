package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public CommandLineRunner initData(PlayereRepository playereRepository,
                                    GameRepository gameRepository,
                                    GamePlayerRepository  gamePlayerRepository,
                                    ShipRepository  shipRepository,
                                    SalvoRepository salvoRepository,
                                    ScoreRepository scoreRepository) {
    return (args) -> {



      Player playe1  = new Player("david@gmail.com",passwordEncoder().encode("admin"));
      Player  playe2  = new Player("rocket@gmail.com",passwordEncoder().encode("root"));

      playereRepository.save(playe1);
      playereRepository.save(playe2);

      Game game1 = new Game();
      Game game2 = new Game();
      game2.setCreated(Date.from(game1.getCreated().toInstant().plusSeconds(3600)));
      Game game3 = new Game();
      game3.setCreated(Date.from(game1.getCreated().toInstant().plusSeconds(7200)));

      gameRepository.save(game1);
      gameRepository.save(game2);
      gameRepository.save(game3);

      GamePlayer  gamePlayer1 = new GamePlayer(game1,playe1);
      GamePlayer  gamePlayer2 = new GamePlayer(game1,playe2);
      GamePlayer  gamePlayer3 = new GamePlayer(game2,playe2);
      GamePlayer  gamePlayer4 = new GamePlayer(game2,playe1);

      gamePlayerRepository.save(gamePlayer1);
      //gamePlayerRepository.save(gamePlayer2);
      gamePlayerRepository.save(gamePlayer3);
      gamePlayerRepository.save(gamePlayer4);

      String battleship = "Battleship";
      String submarine = "Submarine";
      String destroyer = "Destroyer";
      String patrolBoat = "Patrol Boat";
      Ship ship1 = new Ship(destroyer, Arrays.asList("H2", "H3", "H4"),gamePlayer1);
      Ship ship2 = new Ship(submarine, Arrays.asList("E1", "F1", "G1"),gamePlayer1);
      Ship ship3 = new Ship(patrolBoat, Arrays.asList("B4", "B5"),gamePlayer1);
      Ship ship4 = new Ship(destroyer, Arrays.asList("B5", "C5", "D5"),gamePlayer2);
      Ship ship5 = new Ship(patrolBoat, Arrays.asList("F1", "F2"),gamePlayer2);

      shipRepository.save(ship1);
      shipRepository.save(ship2);
      shipRepository.save(ship3);
      //shipRepository.save(ship4);
      //shipRepository.save(ship5);

      Salvo salvo1 = new Salvo(1,Arrays.asList("H2", "H3", "H4"),gamePlayer1);
      Salvo salvo2 = new Salvo(1,Arrays.asList("E1", "F1", "G1"),gamePlayer2);

      salvoRepository.save(salvo1);
      //salvoRepository.save(salvo2);

      Score score1 = new Score(playe1,game1,1.0D,new Date());
      Score score2 = new Score(playe2,game1,0.0D,new Date());

      scoreRepository.save(score1);
      scoreRepository.save(score2);

    };
  }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  PlayereRepository playereRepository;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(inputName-> {
      Player player = playereRepository.findByEmail(inputName).get();
      if (player != null) {
        return new User(player.getEmail(), player.getPassword(),
                AuthorityUtils.createAuthorityList("USER"));
      } else {
        throw new UsernameNotFoundException("Unknown user: " + inputName);
      }
    });
  }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/web/**").permitAll()
            .antMatchers("/api/game_view/*").hasAuthority("USER")
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/api/games").permitAll();

    http.formLogin()
            .usernameParameter("name")
            .passwordParameter("pwd")
            .loginPage("/api/login");

    http.logout().logoutUrl("/api/logout");

    // turn off checking for CSRF tokens
    http.csrf().disable();
    http.headers().frameOptions().disable();

    // if user is not authenticated, just send an authentication failure response
    http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if login is successful, just clear the flags asking for authentication
    http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

    // if login fails, just send an authentication failure response
    http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if logout is successful, just send a success response
    http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
  }

  private void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
  }
}





