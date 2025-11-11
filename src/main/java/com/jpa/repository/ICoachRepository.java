package com.jpa.repository;

import com.jpa.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ICoachRepository extends JpaRepository<Coach, Long> {

    Set<Coach> findByNameCoachContainingIgnoreCase(String nameCoach);
    Set<Coach> findByLastNameContainingIgnoreCase(String lastName);
    Set<Coach> findByClubIsNull(); // Entrenadores sin club

    long countByCountry_IdCountry(Long countryId);

}