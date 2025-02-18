package com.cpstablet.tablet.DTO.commonInfoDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubobjectCommonInfDTO {

    private Long id;
    private String numberKO;
    private String subObjectName;
    private Long comments;
    private String status;
    private List<SystemCommonInfDTO> data;

}
