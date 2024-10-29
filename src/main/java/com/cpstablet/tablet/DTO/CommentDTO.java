package com.cpstablet.tablet.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private Long serialNumber;
    private String subobject;
    private Date startDate;
    private Date endDatePlan;
    private Date endDateFact;
    private String commentCategory;
    private String description;


}
