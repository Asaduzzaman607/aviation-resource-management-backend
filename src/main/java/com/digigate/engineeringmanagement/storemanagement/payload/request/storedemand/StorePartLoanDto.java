package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StorePartLoanDto implements IDto {
    private Long id;
    private String loanNo;
    private String remarks;
    @NotNull
    private LocalDate loanExpires;
    @NotNull
    private LocalDate updateDate;
    @NotNull
    private Long vendorId;
    private String attachment;
    @Valid
    private List<StorePartLoanDetailDto> storePartLoanDetailDtoList;

}
