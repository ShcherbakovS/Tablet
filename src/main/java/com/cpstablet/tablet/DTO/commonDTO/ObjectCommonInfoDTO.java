package com.cpstablet.tablet.DTO.commonDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectCommonInfoDTO {

    private Long systemsPNRTotalQuantity;
    private Long systemsPNRQuantityAccepted;
    private Long actsIITotalQuantity;
    private Long actsIISignedQuantity;
    private Long actsKOTotalQuantity;
    private Long actsKOSignedQuantity;
    private Long commentsTotalQuantity;
    private Long commentsNotResolvedQuantity;
    private Long defectiveActsTotalQuantity;
    private Long defectiveActsNotResolvedQuantity;
    private Long busyStaff;



}
