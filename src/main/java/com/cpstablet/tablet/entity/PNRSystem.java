package com.cpstablet.tablet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PNRSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long PNRSystemId;
    private String PNRSystemName;
    private String PNRSystemRD;
    private String PNRSystemII;
    private String PNRSystemKO;
    private String CCSNumber;
    private String PNRSystemStatus;
    private LocalDate PNRPlanDate;
    private LocalDate PNRFactDate;
    private LocalDate IIPlanDate;
    private LocalDate IIFactDate;
    private LocalDate KOPlanDate;
    private LocalDate KOFactDate;
    private String CIWExecutor;
    private String CWExecutor;

}
