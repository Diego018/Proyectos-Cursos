package com.jpa.repository;

import com.jpa.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IPositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByDescPosition(String descPosition);
    Set<Position> findByDescPositionContainingIgnoreCase(String descPosition);
    boolean existsByDescPosition(String descPosition);

}