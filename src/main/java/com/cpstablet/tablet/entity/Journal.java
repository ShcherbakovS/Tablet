package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "username")
    private String user;

    @Column(name = "organisation")
    private String organisation;

    @Column(name = "subObject")
    private String subObject;

    @Column(name = "system")
    private String system;
    @ManyToOne
    @JoinColumn(name = "capitalcs_id")
    private CapitalCS capitalCS;

    @Column
    private String date;

    private Long serialNumber;
}
