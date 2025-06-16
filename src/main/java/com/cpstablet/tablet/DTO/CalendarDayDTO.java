package com.cpstablet.tablet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayDTO {

    private Long id;

    private Long personnelPlan;

    private Long personnelFact;

    private String date;

}
