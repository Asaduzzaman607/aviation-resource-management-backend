package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuotationNoListDto;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Value(staticConstructor = "of")
public class CsResponseDto {
    Long rfqId;
    String csNo;
    LocalDateTime createdAt;
    Boolean isRejected;
    String rejectedDesc;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListAudit;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListFinal;
    Set<Long> quotationIdList;
    List<QuotationNoListDto> quotationNoListDto;
    List<CsItemPartResponseDto> csItemPartResponseDtoList;
    List<CsVendorResponseDto> csVendorResponseDtoList;
    String remarks;
}
