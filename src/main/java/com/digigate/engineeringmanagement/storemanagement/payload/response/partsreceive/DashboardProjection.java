package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import com.digigate.engineeringmanagement.planning.constant.PartStatus;

public interface DashboardProjection {
    Integer getTotal();

    Integer getMnth();
    Integer getYr();

    Integer getPartClassification();

    PartStatus getPartStatus();
}
