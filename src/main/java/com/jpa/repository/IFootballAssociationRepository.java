package com.jpa.repository;

import com.jpa.entity.FootballAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IFootballAssociationRepository extends JpaRepository<FootballAssociation, Long> {

    Optional<FootballAssociation> findByNameAssociation(String nameAssociation);
    boolean existsByNameAssociation(String nameAssociation);

    Object countByCountry_IdCountry(Long idCountry);
}
