package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Setter
@Getter
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
    private String executor; // Организация исполнитель
    @Column(name = "user_name")
    private String userName; // ФИО лица выдавшего замечание
    @Column(name = "user_organisation")
    private String userOrganisation; // организация лица выдавшего замечание
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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    Photo photo;

}
