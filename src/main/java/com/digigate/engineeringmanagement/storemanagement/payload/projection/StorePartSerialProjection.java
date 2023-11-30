package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface StorePartSerialProjection {
    Long getId();
    Long getSerialId();
    String getSerialSerialNumber();
    Integer getQuantity();
    boolean getIsActive();
}
