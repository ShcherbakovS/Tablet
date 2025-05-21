package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
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
    private String PNRPlanDate;
    private String PNRFactDate;
    private String IIPlanDate;
    private String IIFactDate;
    private String KOPlanDate;
    private String KOFactDate;
    private String CIWExecutor;
    private String CWExecutor;

    // TODO Миграция ликви
    @ManyToOne
    @JoinColumn(name = "subObjectId")
    private SubObject subObject;

}
