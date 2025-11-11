package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

    private Long idCountry;
    private String nameCountry;
    private Long totalAssociations;
    private Long totalPlayers;
    private Long totalCoaches;

}
