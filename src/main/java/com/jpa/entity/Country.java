package com.jpa.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_country")
    private Long idCountry;

    @Column(name = "name_country")
    private String nombreCountry;

    @OneToMany(targetEntity = FootballAssociation.class ,mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FootballAssociation> footballAssociations = new HashSet<>();

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, mappedBy = "country")
    private Set<Player> players = new HashSet<>();

    @OneToMany(targetEntity = Coach.class, fetch = FetchType.LAZY, mappedBy = "country")
    private Set<Coach> coachs = new HashSet<>();

}
