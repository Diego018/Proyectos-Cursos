package com.jpa.repository;

import com.jpa.entity.FootballCompetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IFootballCompetitionRepository extends JpaRepository<FootballCompetition, Long> {

    Optional<FootballCompetition> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);



}