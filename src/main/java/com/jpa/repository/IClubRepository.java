package com.jpa.repository;

import com.jpa.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByNameClub(String nameClub);
    Set<Club> findByNameClubContainingIgnoreCase(String nameClub);
    boolean existsByNameClub(String nameClub);

}