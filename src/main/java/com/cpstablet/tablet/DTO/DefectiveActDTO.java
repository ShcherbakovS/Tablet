package com.cpstablet.tablet.DTO;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefectiveActDTO {
    private Long id;
    private Long serialNumber;
    private String iiNumber;
    private String subObject;
    private String systemName;
    private String equipment;
    private String description;
    private String defectiveActStatus;
    private String executor;
    private String userName;
    private String startDate;
    private String endDatePlan;
    private String endDateFact;
    private String defectiveActExplanation;
    private String codeCCS;
    private String manufacturerNumber;
    private String manufacturer;
}
