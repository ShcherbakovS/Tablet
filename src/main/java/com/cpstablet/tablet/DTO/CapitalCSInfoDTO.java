package com.cpstablet.tablet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapitalCSInfoDTO {

    private String workingDocsLink; //рабочая документация
    private String executiveDocsLink; // исполнительная док-ция
    private String operationalDocsLink; // эксплуотационная док -ция
    private String preparatoryDocsLink; // подготовительная док-ция

}
