package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsDto implements IDto {
    private RfqType rfqType;
    @Builder.Default
    private OrderType orderType = OrderType.PURCHASE;
    @NotNull
    private Long rfqId;
    @NotEmpty
    private Set<Long> quotationIdList;
    @NotEmpty
    private Set<CsPartDetailDto> csPartDetailDtoSet;

    private String remarks;
    private Long existingCsId;
}
