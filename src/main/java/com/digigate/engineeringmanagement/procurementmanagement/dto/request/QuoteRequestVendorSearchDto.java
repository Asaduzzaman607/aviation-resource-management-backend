package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.Data;

@Data
public class QuoteRequestVendorSearchDto implements SDto {
    private Long id;
    private Boolean isActive = true;
    private VendorType vendorType;
    private Long quoteRequestId;
}
