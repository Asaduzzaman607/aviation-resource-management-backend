package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorQuotationFeeInvoiceDto implements IDto {
    private Long id;
    @NotBlank
    private String feeName;
    @NotNull
    private Double feeCost;
    private VendorRequestType vendorRequestType;
    private Long currencyId;
    private Boolean isActive;
}
