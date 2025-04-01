package com.cpstablet.tablet.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoDTO {

    private Long id;
    private String fullName;
    private String phoneNumber;
    private String organisation;


}
