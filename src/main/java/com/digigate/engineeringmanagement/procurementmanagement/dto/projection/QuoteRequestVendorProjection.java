package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;

public interface QuoteRequestVendorProjection {
    Long getId();

    Long getVendorId();

    String getVendorName();

    VendorType getVendorVendorType();

    String getVendorAddress();

    String getVendorEmail();

    String getVendorWebsite();

    String getVendorOfficePhone();

    String getVendorContactPerson();

    String getVendorContactSkype();
}
