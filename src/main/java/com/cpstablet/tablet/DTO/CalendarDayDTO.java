package com.cpstablet.tablet.DTO;

import lombok.Builder;



@Builder
public class CalendarDayDTO {

    private Long id;

    private Long personnelPlan;

    private Long personnelFact;

    private String date;


}
