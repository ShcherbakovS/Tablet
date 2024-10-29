package com.cpstablet.tablet.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    // TODO:  замена на объект ManyToOne
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;
    @Column(name = "serial_number")
    private Long serialNumber;
    @Column(name = "subobject")
    private String subobject;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date_plan")
    private Date endDatePlan;
    @Column(name = "end_date_fact")
    private Date endDateFact;
    @Column(name = "comment_category")
    private String commentCategory;
    @Column(name = "description")
    private String description;



}
