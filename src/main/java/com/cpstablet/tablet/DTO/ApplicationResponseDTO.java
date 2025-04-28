package com.cpstablet.tablet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApplicationResponseDTO {

    private Long id;
    private String username;
    private Long userId;
    private String role;
    private String organisation;
    private String fullName;
    private List<CapitalCSDTO> objectsToAdd;
    private String description;

}
