package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface StoreIssueItemProjection {

    Long getId();
    Integer getIssuedQuantity();

    Long getStoreDemandItemPartId();
    Long getStoreDemandItemStoreDemandId();
}
