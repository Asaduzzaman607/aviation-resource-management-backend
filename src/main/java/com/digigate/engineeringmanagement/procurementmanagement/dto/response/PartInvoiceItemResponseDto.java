package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.procurementmanagement.constant.PaymentMode;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartInvoiceItemResponseDto {
    private Long id;
    private Long poItemId;
    private Long partInvoiceId;
    private PaymentMode paymentMode;
    private String remarks;
    private Boolean isPartiallyApproved;
    private Long paymentCurrencyId;
    private String paymentCurrencyCode;
    private Integer alreadyApprovedQuantity;
    private Integer quantity;
    private Long uomId;
    private String uomCode;
    private Long partId;
    private String partNo;
    private String partDescription;
    private PriorityType priorityType;
    private Double unitPrice;
    private String condition;
    private String leadTime;
    private Long currencyId;
    private String currencyCode;
    private Long airCraftId;
    private String airCraftName;
    private Long vendorId;
    private String vendorName;
}
