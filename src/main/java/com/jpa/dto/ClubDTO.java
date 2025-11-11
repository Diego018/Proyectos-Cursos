
package com.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubDTO {


    private Long idClub;
    private String nameClub;
    private String coachName;
    private Long coachId;
    private String associationName;
    private Long associationId;
    private Integer totalPlayers;


}
