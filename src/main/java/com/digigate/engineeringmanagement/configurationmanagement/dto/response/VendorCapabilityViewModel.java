package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class VendorCapabilityViewModel {
    private Long id;
    private Long vendorCapabilityId;
    private boolean status;
}
