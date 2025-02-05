package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//TODO: переписать поля сущностей со строковых значений на ссылки на объекты
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapitalCS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "capitalcs_id")
    private Long capitalCSId;
    private String capitalCSName;

    @Column(name = "code_ccs")
    private String codeCCS;

    private String locationRegion;
    private String objectType;
    private String customer;
    private String executorOfPNR;


}
