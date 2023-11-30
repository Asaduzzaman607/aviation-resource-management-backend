package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;


public interface StorePartAvailabilitySearchProjection {
    Long getId();

    Long getPartId();

    String getPartNo();

    Integer getQuantity();

    Integer getDemandQuantity();

    Integer getIssuedQuantity();

    Integer getRequisitionQuantity();

    Integer getUomWiseQuantity();

    Long getUomId();

    String getUomCode();

    Long getOfficeId();

    String getOfficeCode();
    Integer getMaxStock();
    Integer getMinStock();

}
