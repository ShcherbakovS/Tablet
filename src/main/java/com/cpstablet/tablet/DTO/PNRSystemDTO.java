package com.cpstablet.tablet.DTO;


import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class PNRSystemDTO {


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
