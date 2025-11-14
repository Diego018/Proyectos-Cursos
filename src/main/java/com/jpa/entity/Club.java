package com.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_club")
    private Long idClub;

    @Column(name = "name_club")
    private String nameClub;

    @OneToOne(targetEntity = Coach.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach")
    private Coach coach;

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, mappedBy = "club", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Player> players = new HashSet<>();

    @ManyToOne(targetEntity = FootballAssociation.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fotball_association")
    private FootballAssociation footballAssociation;

    @ManyToMany(targetEntity = FootballCompetition.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "club_competition",
            joinColumns = @JoinColumn(name = "id_club"),
            inverseJoinColumns = @JoinColumn(name = "id_football_competition")
    )
    @ToString.Exclude
    private Set<FootballCompetition> footballCompetitions = new HashSet<>();
}
