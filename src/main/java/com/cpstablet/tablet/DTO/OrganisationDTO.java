package com.cpstablet.tablet.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrganisationDTO {

    @JsonIgnore
    private Long id;

    private String organisationName;
}
