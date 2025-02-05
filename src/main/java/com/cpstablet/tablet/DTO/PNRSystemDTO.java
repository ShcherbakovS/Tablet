package com.cpstablet.tablet.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
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

//"pnrsystemII", "ciwexecutor", "cwexecutor", "pnrplanDate", "pnrfactDate", "pnrsystemStatus", "iiplanDate", "iifactDate", "koplanDate",
// "ccsnumber", "pnrsystemId", "pnrsystemKO", "pnrsystemName", "pnrsystemRD", "kofactDate"
public class PNRSystemDTO {

    private Long PNRSystemId;

    private String PNRSystemName;
    private String PNRSystemRD;
    private String PNRSystemII;
    private String PNRSystemKO;
    private String CCSNumber;
    private String PNRSystemStatus;
    private String PNRPlanDate;
    private String PNRFactDate;
    private String IIPlanDate;
    private String IIFactDate;
    private String KOPlanDate;
    private String KOFactDate;
    private String CIWExecutor;
    private String CWExecutor;

}
