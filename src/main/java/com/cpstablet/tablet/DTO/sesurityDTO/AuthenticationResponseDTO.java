package com.cpstablet.tablet.DTO.sesurityDTO;

import lombok.Getter;

@Getter
public class AuthenticationResponseDTO {

    private final String accessToken;
    private final String refreshToken;

    public AuthenticationResponseDTO(String accessToken, String refreshToken)  {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
