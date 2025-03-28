package com.cpstablet.tablet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String userInfo;
    private String phoneNumber;
    private String email;
    private Long organisationId;



}
