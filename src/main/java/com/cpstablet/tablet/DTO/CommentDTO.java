package com.cpstablet.tablet.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {

    private Long serialNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int iiNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subObject;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String systemName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String startDate;
    private String endDatePlan;
    private String endDateFact;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentCategory;
    private String commentExplanation;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String codeCCS;

}
