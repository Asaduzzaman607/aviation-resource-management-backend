package com.digigate.engineeringmanagement.logistic.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


@Builder
@Getter
@Setter
@AllArgsConstructor
public class DutyFeeRequestDto implements IDto {

    @NotNull
    private Long partsInvoiceItemId;
    private Set<String> attachment;
    private List<DutyFeeItemRequestDto> dutyFeeItemRequestDtoList;

}
