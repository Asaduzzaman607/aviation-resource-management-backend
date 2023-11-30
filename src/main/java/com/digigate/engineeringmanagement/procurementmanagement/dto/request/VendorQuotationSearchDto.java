package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.Data;

@Data
public class VendorQuotationSearchDto implements SDto {
    private String query= ApplicationConstant.EMPTY_STRING;
    private Long rfqId;
    private Boolean isActive = true;
    private RfqType rfqType;
    private InputType inputType = InputType.CS;
}
