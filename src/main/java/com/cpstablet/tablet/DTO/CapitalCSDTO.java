package com.cpstablet.tablet.DTO;

import lombok.Data;

@Data
public class CapitalCSDTO {

    private String capitalCSName;
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

}
