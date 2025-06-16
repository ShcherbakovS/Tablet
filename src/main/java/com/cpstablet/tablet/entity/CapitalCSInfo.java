package com.cpstablet.tablet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CapitalCSInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String workingDocsLink; //рабочая документация
    private String executiveDocsLink; // исполнительная док-ция
    private String operationalDocsLink; // эксплуотационная док -ция
    private String preparatoryDocsLink; // подготовительная док-ция

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capitalcs_id")
    private CapitalCS capitalCS;

}
