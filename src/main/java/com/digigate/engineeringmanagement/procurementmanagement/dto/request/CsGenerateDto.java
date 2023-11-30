package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsDetail;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsPartDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsGenerateDto implements IDto {
    @NotNull
    private Long rfqId;
    private String csNo;
    private LocalDateTime createdAt;
    @NotEmpty
    private Set<Long> quotationIdList;
    private List<QuotationNoListDto> quotationNoListDtoList;
    private RfqType rfqType;
    private Boolean isRejected;
    private String rejectedDesc;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListAudit;
    List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListFinal;
    @JsonIgnore
    private Set<CsDetail> csDetailSet = new HashSet<>();
    @JsonIgnore
    private Set<CsPartDetail> csPartDetailSet = new HashSet<>();
}