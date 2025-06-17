package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "defective_act")
public class DefectiveAct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "serial_number") // порядковый номер
    private Long serialNumber;

    @Column(name = "ii_number") // номер акта ИИ
    private String iiNumber;

    @Column(name = "subobject")// Подобъект
    private String subObject;

    @Column(name = "system_name")// Система
    private String systemName;

    @Column(name = "equipment")// Оборудование
    private String equipment;

    @Column(name = "description") //содержание замечания
    private String description;

    @Column(name = "defective_act-status") // статус дефектного акта
    private String defectiveActStatus;

    @Column(name = "executor")
    private String executor;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "start_date") // Дата выдачи замечания
    private String startDate;

    @Column(name = "end_date_plan") // плановая дата устранения
    private String endDatePlan;

    @Column(name = "end_date_fact") // фактическая дата устранения
    private String endDateFact;

    @Column(name = "defective_act_explanation")
    private String defectiveActExplanation;

    @Column(name = "code_ccs") // номер объекта
    private String codeCCS;

    @Column(name = "manufacturer_number")
    private String manufacturerNumber; //заводской номер

    @Column(name = "manufacturer")
    private String manufacturer; // изготовитель

    @OneToMany( fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Photo> photos;

}
