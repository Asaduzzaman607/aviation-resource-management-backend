package com.digigate.engineeringmanagement.configurationmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.Data;

@Data
public class CapabilitySearchDto implements SDto {
    private String query;
    private VendorType type = VendorType.MANUFACTURER;
    private boolean isActive = true;
}
