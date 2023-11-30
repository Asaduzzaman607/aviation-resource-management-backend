package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorCapabilityLogRequestDto implements IDto {
    private Long id;
    private Long vendorCapabilityId;
    private Long vendorId;
    private boolean status = true;
}
