package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsPartDetailResponseDto {
    private Long id;
    private Long iqItemId;
    private Long itemId;
    private Long partId;
    private String partNo;
    private String partDescription;
    private String moqRemark;

    private List<CsAuditDisposalResponseDto> csAuditDisposalResponseDtoList;
}
