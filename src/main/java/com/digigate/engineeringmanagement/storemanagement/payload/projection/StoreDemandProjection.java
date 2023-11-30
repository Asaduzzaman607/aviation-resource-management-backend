package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import com.digigate.engineeringmanagement.storemanagement.constant.DepartmentType;

public interface StoreDemandProjection {
    Long getId();
    String getVoucherNo();
    DepartmentType getDepartmentType();
}
