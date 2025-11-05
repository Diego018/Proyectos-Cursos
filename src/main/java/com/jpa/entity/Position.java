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
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_position")
    private Long id_position;

    @Column(name = "desc_position")
    private String descPosition;

    @OneToMany(targetEntity = Player.class, fetch = FetchType.LAZY, mappedBy = "positions")
    private List<Player> players;

}
