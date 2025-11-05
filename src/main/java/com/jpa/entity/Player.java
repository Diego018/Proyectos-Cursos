package com.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_player;

    @Column(name = "name_player")
    private String namePlayer;

    @Column(name = "last_name")
    private String lastName;
    private String nationality;
    private Integer age;
    private String position;

    @ManyToOne(targetEntity = Club.class)
    @JoinColumn(name = "id_club")
    private Club club;

    @ManyToOne(targetEntity = Position.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_position")
    private Position positions;


}
