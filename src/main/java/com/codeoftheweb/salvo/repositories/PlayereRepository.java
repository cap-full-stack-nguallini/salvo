package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayereRepository  extends JpaRepository<Player,   Long> {
}