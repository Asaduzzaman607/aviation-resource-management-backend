package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;

import java.time.LocalDate;

public interface QuoteRequestProjection {
    Long getId();
    String getRfqNo();
    RfqType getRfqType();
    Long getRequisitionId();
    Long getPartOrderId();
    String getPartOrderOrderNo();
    LocalDate getUpdateDate();
    InputType getInputType();
}
