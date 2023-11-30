package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;

public interface StorePartAvailabilityProjection {
    Long getId();
    Integer getQuantity();
    Long getPartId();

    PartClassification getPartClassification();

    Long getPartModelAircraftModelId();

    String getPartModelAircraftModelAircraftModelName();
}
