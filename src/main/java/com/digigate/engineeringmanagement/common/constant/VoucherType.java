package com.digigate.engineeringmanagement.common.constant;

public enum VoucherType {
    DEMAND,
    ISSUE,
    REQ,
    RFQ,
    QUOTE,
    STORE_PART_LOAN,
    SCRAP,
    CS,
    INSPECTOR,
    INSPECTION,
    CHECKLIST,
    ORDER,
    RETURN,
    USBA_STR,
    SIB,
    PI,
    PO,
    LO,
    RO,
    USBA_C,
    TRACKER;

    @Override
    public String toString() {
        if (this == USBA_STR) return ApplicationConstant.WORK_ORDER_NO_INITIAL;
        else if (this == USBA_C) return ApplicationConstant.ALTER_VOUCHER_INITIAL;
        return this.name();
    }
}
