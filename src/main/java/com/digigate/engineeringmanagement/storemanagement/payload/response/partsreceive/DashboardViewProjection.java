package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import java.time.LocalDate;

public interface DashboardViewProjection {
    Long getPartId();

    String getPartNo();

    Integer getTotal();

    LocalDate getExpireDate();

    String getAcType();

    String getNomenclature();

    LocalDate getInspDate();

    String getSerialNo();

    Integer getQty();
    String getUom();
    LocalDate getShelfLife();
    String getGrnNo();

}
