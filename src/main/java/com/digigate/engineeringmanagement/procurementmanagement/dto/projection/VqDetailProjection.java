package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import com.digigate.engineeringmanagement.procurementmanagement.constant.ExchangeType;

public interface VqDetailProjection {
    Long getId();
    Long getRequisitionItemDemandItemPartId();
    Long getAlternatePartId();
    Long getPoItemIqItemRequisitionItemDemandItemPartId();
    Long getPoItemIqItemAlternatePartId();
    Double getUnitPrice();
    Double getMoq();
    Double getMlv();
    Double getMov();
    String getLeadTime();
    String getIncoterms();
    String getCurrencyCode();
    String getCondition();
    String getUnitMeasurementCode();
    Integer getPartQuantity();
    Double getExchangeFee();
    Double getRepairCost();
    Double getBerLimit();
    Double getDiscount();
    ExchangeType getExchangeType();
}
