package com.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FootballAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fotball_association")
    private Long id_fotballAssociation;

    @Column(name = "name_association", nullable = false, unique = true)
    private String nameAssociation;

    private String president;

    @OneToMany(targetEntity = Club.class, mappedBy = "footballAssociation", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Club> clubs = new HashSet<>();

    @ManyToOne(targetEntity = Country.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_country", nullable = false)
    private Country country;
}
