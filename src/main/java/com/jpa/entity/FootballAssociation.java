package com.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FootballAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fotball_association")
    private Long id_fotballAssociation;

    private String name;
    private String country;
    private String president;

    @OneToMany(targetEntity = Club.class, mappedBy = "footballAssociation")

    private List<Club> clubs;

}
