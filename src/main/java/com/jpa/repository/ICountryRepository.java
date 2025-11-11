package com.jpa.repository;

import com.jpa.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByNombreCountry(String nombreCountry);
    boolean existsByNombreCountry(String nombreCountry);

}
