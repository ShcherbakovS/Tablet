package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapitalCS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "capitalcs_id")
    private Long capitalCSId;
    private String capitalCSName;
    @Column(name = "code_ccs")
    private String codeCCS;
    private String locationRegion;
    private String objectType;
    // заказчик
    private String customer;
    // исполнитель СМР
    private String CIWExecutor;
    // исполнитель ПНР
    private String CWExecutor;
    // Куратор заказчика
    private String customerSupervisor;
    // Куратор ПНР
    private String CWSupervisor;
    // куратор СМР
    private String CIWSupervisor;
    // счетчик замечаний
    private Long commentCounter;

}
