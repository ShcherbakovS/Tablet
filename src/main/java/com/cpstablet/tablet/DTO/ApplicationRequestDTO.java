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
public class ApplicationRequestDTO {


    private List<String> objectsToAdd;
    private String description;
}
