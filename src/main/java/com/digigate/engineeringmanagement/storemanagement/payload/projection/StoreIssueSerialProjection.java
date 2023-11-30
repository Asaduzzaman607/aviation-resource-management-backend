package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface StoreIssueSerialProjection {
    Long getId();
    Long getStoreIssueItemId();
    String getGrnNo();
    Integer getQuantity();
    Long getStorePartSerialId();
    String getStorePartSerialSerialSerialNumber();
    Double getStorePartSerialPrice();
}
