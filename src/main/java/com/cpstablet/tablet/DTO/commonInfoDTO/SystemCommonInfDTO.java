package com.cpstablet.tablet.DTO.commonInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private String PNRPlanDate;
    private String PNRFactDate;
    private String IIPlanDate;
    private String IIFactDate;
    private String KOPlanDate;
    private String KOFactDate;
    private String CIWExecutor;
    private String CWExecutor;

}
