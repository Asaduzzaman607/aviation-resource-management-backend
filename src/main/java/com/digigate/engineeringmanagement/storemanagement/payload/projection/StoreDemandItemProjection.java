package com.digigate.engineeringmanagement.storemanagement.payload.projection;

public interface StoreDemandItemProjection {
    Long getId();
    String getStoreDemandInternalDepartmentCode();
    String getStoreDemandVendorName();
    String getStoreDemandAircraftAircraftName();
    String getPartPartNo();
    Long getPartId();
}
