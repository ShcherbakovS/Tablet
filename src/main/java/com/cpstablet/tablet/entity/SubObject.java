package com.cpstablet.tablet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subObjectId;

    private String subObjectName;

    private String numberKO;

    private String status;

    private String CCSCode;


}
