package com.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Va a generar el id automaticamente
    private Long id;
    private String name;

    @Column(name = "last_name")
    private String lastName;
    private String nationality;
    private Integer age;

}
