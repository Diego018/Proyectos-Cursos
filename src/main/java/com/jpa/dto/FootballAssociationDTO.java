package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FootballAssociationDTO {

    private Long idAssociation;
    private String nameAssociation;
    private String president;
    private String countryName;
    private Long countryId;
    private Integer totalClubs;

}