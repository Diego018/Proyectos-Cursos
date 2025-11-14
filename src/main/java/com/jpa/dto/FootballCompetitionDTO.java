package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballCompetitionDTO {

    private Long idCompetition;
    private String name;
    private Integer cuantityPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalClubs;
    private Set<String> competitionClubs;
}