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
public class PNRSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long PNRSystemId;
    private String PNRSystemName;

}
