package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.planning.payload.response.AlternatePartViewModel;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CsItemPartResponseDto {
    private Long id;
    private Long itemId;
    private PriorityType priority;
    private Long partId;
    private String partNo;
    private String partDescription;
    private List<AlternatePartViewModel> alternate = new ArrayList<>();
    private Integer qty;
    private Long uomId;
    private String uomCode;
    private String moqRemark;
    List<CsAuditDisposalResponseDto> comments;
}
