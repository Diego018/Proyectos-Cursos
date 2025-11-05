package com.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_club;

    @Column(name = "name_club")
    private String nameClub;

    @OneToOne(targetEntity = Coach.class, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_coach")
    private Coach coach;

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, mappedBy = "club")
    private List<Player> players;

    @ManyToOne(targetEntity = FootballAssociation.class)
    @JoinColumn(name = "id_fotball_association")
    private FootballAssociation footballAssociation;

    @ManyToMany(targetEntity = FootballCompetition.class, fetch = FetchType.LAZY)
    @JoinTable(name = "club_competition", joinColumns = @JoinColumn(name = "id_club"),inverseJoinColumns = @JoinColumn(name = "id_fotball_competition"))
    private List<FootballCompetition> footballCompetitions;

}
