package com.digigate.engineeringmanagement.logistic.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class DutyFeeItemRequestDto implements IDto {

    private long id;
    private String fees;
    private Double totalAmount;
    private Long dutyFeeId;
    @NotNull
    private Long currencyId;
    private Boolean isActive;
}
