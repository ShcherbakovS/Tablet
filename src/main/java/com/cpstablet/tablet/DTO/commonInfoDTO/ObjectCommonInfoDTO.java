package com.cpstablet.tablet.DTO.commonInfoDTO;


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
    private Long systemsPNRDynamic;
    private Long actsIITotalQuantity;
    private Long actsIISignedQuantity;
    private Long actsIIDynamic;
    private Long actsKOTotalQuantity;
    private Long actsKOSignedQuantity;
    private Long actsKODynamic;
    private Long commentsTotalQuantity;
    private Long commentsNotResolvedQuantity;
    private Long defectiveActsTotalQuantity;
    private Long defectiveActsNotResolvedQuantity;
    private Long busyStaff;



}
