package com.cpstablet.tablet.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long commentId;
    private Long serialNumber;
    private String iiNumber;
    private String subObject;
    private String systemName;
    private String description;
    private String commentStatus;
    private String executor;
    private String userName;
    private String startDate;
    private String endDatePlan;
    private String endDateFact;
    private String commentCategory;
    private String commentExplanation;
    private String codeCCS;

}
