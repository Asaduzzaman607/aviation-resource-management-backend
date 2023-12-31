package com.digigate.engineeringmanagement.configurationmanagement.dto.projection;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;

import java.time.LocalDate;

public interface VendorProjection {
    Long getId();
    String getName();
    VendorType getVendorType();
    LocalDate getValidTill();
    String getWorkFlowActionName();
}
