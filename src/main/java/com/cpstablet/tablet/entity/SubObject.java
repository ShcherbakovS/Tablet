package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "subObject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PNRSystem> PNRSystems;

    private String status;

    private String CCSCode;


}
