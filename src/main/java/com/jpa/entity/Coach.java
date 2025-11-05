package com.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_coach;

    @Column(name = "name_coach")
    private String nameCoach;

    @Column(name = "last_name")
    private String lastName;
    private String nationality;
    private Integer age;


}
