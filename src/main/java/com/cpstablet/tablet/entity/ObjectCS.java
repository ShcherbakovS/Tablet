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
public class ObjectCS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long objectKSId;
    private String objectKSName;
    private String codeOKS;
    private String locationRegion;
    private String objectType;
    private String customer;
    private String executorOfPNR;


    // TODO: настроить связь, проверить
    @OneToMany(fetch = FetchType.LAZY)
    List<Comment> comments;


}
