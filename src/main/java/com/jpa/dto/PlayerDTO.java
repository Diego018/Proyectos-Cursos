package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private Long idPlayer;
    private String namePlayer;
    private String lastName;
    private Integer age;
    private String clubName;
    private Long clubId;
    private String positionName;
    private Long positionId;
    private String countryName;
    private Long countryId;

}