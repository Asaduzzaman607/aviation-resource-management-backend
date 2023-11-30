package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorCapabilityResponseDto {
    private Long id;
    private Long vendorCapabilityId;
    private String name;
}
