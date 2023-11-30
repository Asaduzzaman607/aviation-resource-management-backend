package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;

public interface PartProjection {
    Long getId();
    String getPartNo();
    String getDescription();
    PartClassification getClassification();
}
