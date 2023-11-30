package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Data
@Builder
public class StorePartLoanDetailDto implements IDto {
    private Long id;
    @NotNull
    private Long serialId;
    private Long storePartLoanId;
    private Boolean isActive = true;
    private String remarks = EMPTY_STRING;
}
