package com.digigate.engineeringmanagement.storemanagement.payload.projection;

import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;

public interface StoreStockInwardProjection {
    Long getId();
    String getVoucherNo();
    Long getInvoiceId();
    RfqType getPartsInvoiceRfqType();
}
