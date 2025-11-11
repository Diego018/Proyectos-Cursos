package com.jpa.repository;

import com.jpa.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IPlayerRepository extends JpaRepository<Player, Long> {

    // Buscar jugadores por nombre y apellido
    Set<Player> findByNamePlayerContainingIgnoreCase(String namePlayer);
    Set<Player> findByLastNameContainingIgnoreCase(String lastName);

    // Buscar jugadores por club
    Set<Player> findByClub_IdClub(Long clubId);

    long countByClub_IdClub(Long clubId);

    long countByCountry_IdCountry(Long countryId);
    long countByPosition_IdPosition(Long positionId);
}
