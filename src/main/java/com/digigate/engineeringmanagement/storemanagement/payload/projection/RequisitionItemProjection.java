package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface RequisitionItemProjection {
    Long getId();
    Long getDemandItemId();
    Long getDemandItemPartId();
    Long getRequisitionQuantity();
}
