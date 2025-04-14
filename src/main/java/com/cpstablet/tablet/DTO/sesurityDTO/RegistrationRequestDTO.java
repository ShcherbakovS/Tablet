package com.cpstablet.tablet.DTO.sesurityDTO;

import lombok.Data;

@Data
public class RegistrationRequestDTO {

    private String email;
    private String password;
    private String fullName;
    private String organisation;

}
