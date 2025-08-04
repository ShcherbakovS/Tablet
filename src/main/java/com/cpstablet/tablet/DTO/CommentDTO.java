package com.cpstablet.tablet.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long commentId;
    private Long serialNumber;
    private String iiNumber;
    private String subObject;
    private String systemName;
    private String description;
    private String commentStatus;
    private String executor;
    // TODO передача через id пользователя
    private String userName;
    // TODO организация- сущность подтягивать по пользователю, запрашивать пользователя по id  при передаче на бэк
    private String organisation;
    private String startDate;
    private String endDatePlan;
    private String endDateFact;
    private String commentCategory;
    private String commentExplanation;
    private String codeCCS;

}
