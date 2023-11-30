package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VendorQuotationFeeInvoiceViewModel {
    private Long id;
    private String feeName;
    private Double feeCost;
    private Long currencyId;
    private String currencyCode;
    private VendorRequestType vendorRequestType;
    private Boolean isActive;
}
