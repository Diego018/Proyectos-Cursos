package com.jpa.repository;

import com.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    // Buscar por email
    Optional<User> findByEmail(String email);

    // Buscar por username
    Optional<User> findByUsername(String username);

    // Buscar por nombre
    Set<User> findByFirstNameIgnoreCase(String firstName);

    // Buscar por apellido
    Set<User> findByLastNameIgnoreCase(String lastName);

    // Buscar por nombre y apellido
    Set<User> findByFirstNameAndLastName(String firstName, String lastName);


    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(:name) OR LOWER(u.lastName) = LOWER(:name)")
    Set<User> findByFirstNameOrLastName(@Param("name") String name);


    boolean existsByEmail(String email);


    boolean existsByUsername(String username);



}