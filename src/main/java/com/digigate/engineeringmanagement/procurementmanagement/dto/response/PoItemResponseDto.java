package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PoItemResponseDto {
    private Long id;
    private Long itemId;
    private Long partId;
    private String partNo;
    private String partDescription;
    private PriorityType requisitionPriority;
    private String Cd;
    private String Lt;
    private Integer quantity;
    private String vendorSerials;
    private Long uomId;
    private String uomCode;
    private Double unitPrice;
    private Long partOrderId;
    private Double discount;
    private  VendorQuotationInvoiceDetailViewModel vendorQuotationInvoiceDetails;
    private String aircraftName;
    private Long aircraftId;
    private Long currencyId;
    private String currencyCode;
}
