package com.digigate.engineeringmanagement.logistic.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DutyFeeResponseDto {

    private Long id;
    private Long partsInvoiceItemId;
    private Long partInvoiceId;
    private String invoiceNo;
    private Long partId;
    private String partNo;
    private List<DutyFeeItemResponseDto> dutyFeeItemList;
    private Set<String> attachment;


}
