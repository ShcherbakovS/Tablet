package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
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
@Table(name = "comment_pnr")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;
    @Column(name = "serial_number") // порядковый номер
    private Long serialNumber;
    @Column(name = "ii_number") // номер акта ИИ
    private String iiNumber;
    @Column(name = "subobject")// Подобъект
    private String subObject;
    @Column(name = "system_name")// Система
    private String systemName;
    @Column(name = "description") //содержание замечания
    private String description;
    @Column(name = "comment_status") // статус замечания
    private String commentStatus;
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
    @Column(name = "comment_category") // категория замечания
    private String commentCategory;
    @Column(name = "comment_explanation")
    private String commentExplanation;
    @Column(name = "code_ccs") // номер объекта
    private String codeCCS;

}
