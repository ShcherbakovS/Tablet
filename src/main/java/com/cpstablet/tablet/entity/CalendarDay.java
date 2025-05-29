package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@Setter
@Getter
@Table(name = "calendar_day")
@AllArgsConstructor
public class CalendarDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private Long id;
    @Column(name ="personnel_plan")
    private Long personnelPlan;
    @Column(name ="personnel_fact")
    private Long personnelFact;
    @Column(name ="date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "capitalcs_id")
    private CapitalCS capitalCS;

}
