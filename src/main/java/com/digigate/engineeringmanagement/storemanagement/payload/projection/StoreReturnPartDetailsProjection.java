package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;

import java.time.LocalDate;

public interface StoreReturnPartDetailsProjection {
    Long getId();

    String getStoreReturnPartStoreReturnVoucherNo();

    String getInstalledPartSerialStorePartAvailabilityPartDescription();

    Long getRemovedPartSerialId();

    String getReasonRemoved();

    String getCaabStatus();

    String getCaabRemarks();

    String getCaabCheckbox();

    String getApprovalAuthNo();

    LocalDate getAuthorizedDate();

    LocalDate getAuthorizesDate();

    String getCertApprovalRef();

    Employee getAuthorizedUser();

    Employee getAuthorizesUser();

    Boolean getCaabEnabled();
}
