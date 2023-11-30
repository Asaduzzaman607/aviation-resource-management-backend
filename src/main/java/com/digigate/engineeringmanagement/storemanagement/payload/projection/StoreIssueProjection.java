package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface StoreIssueProjection {
    Long getId();

    String getVoucherNo();

    String getRegistration();

    Long getStoreDemandId();
}
