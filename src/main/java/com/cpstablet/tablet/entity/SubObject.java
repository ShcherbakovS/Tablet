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
@Table(name = "sub_object_table")
public class SubObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subObjectId;

    private String subObjectName;

    private String numberKO;

    private String status;

    private String CCSCode;


}
