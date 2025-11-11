package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachDTO {

    private Long idCoach;
    private String nameCoach;
    private String lastName;
    private Integer age;
    private String countryName;
    private Long countryId;
    private String clubName;
    private Long clubId;

}
