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
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //EL ID SEA AI (AUTOINCREMENT)
    private Long id;

    @Column(name = "name_team")
    private String nameTeam;

    @OneToOne (targetEntity = Coach.class, cascade = CascadeType.PERSIST)//Va a ser una relacion de uno a uno - la relacion se va a hacer con la clase coach
    @JoinColumn(name = "id_coach") //Para cambiar el nombre de la clave foranea
    private Coach coach;

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, mappedBy = "club")
    private List<Player> players;

    @ManyToOne(targetEntity = FootballAssociation.class)
    private FootballAssociation footballAssociation;

    @ManyToMany(targetEntity = FootballCompetition.class, fetch = FetchType.LAZY)
    @JoinTable(name = "club_Competition", joinColumns = @JoinColumn(name = "id_club"), inverseJoinColumns = @JoinColumn(name = "id_competition"))
    private List<FootballCompetition> footballCompetitions;

}
