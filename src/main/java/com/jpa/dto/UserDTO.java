package com.jpa.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long idUser;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public UserDTO(Long idUser, String firstName, String lastName, String username, String email) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

}
