package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;

public interface ItemProjection {
    Long getId();
    Long getDemandItemId();
    Long getDemandItemPartId();
    PriorityType getDemandItemPriorityType();
    String getDemandItemPartPartNo();
    String getDemandItemPartDescription();
    Long getDemandItemUnitMeasurementId();
    String getDemandItemUnitMeasurementCode();
    Integer getRequisitionQuantity();
}
