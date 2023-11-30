package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface PartWiseUomProjection {
    Long getId();
    Long getPartId();
    Long getUnitMeasurementId();
    String getUnitMeasurementCode();
}
