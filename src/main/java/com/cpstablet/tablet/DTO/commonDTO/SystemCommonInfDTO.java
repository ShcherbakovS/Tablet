package com.cpstablet.tablet.DTO.commonDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemCommonInfDTO {
    private Long PNRSystemId;
    private String CCSNumber;
    private String numberII;
    private String systemName;
    private Long comments;
    private String status;
    private LocalDate PNRPlanDate;
    private LocalDate PNRFactDate;
    private LocalDate IIPlanDate;
    private LocalDate IIFactDate;
    private LocalDate KOPlanDate;
    private LocalDate KOFactDate;
    private String CIWExecutor;
    private String CWExecutor;
    private List<String> statusList;

}
