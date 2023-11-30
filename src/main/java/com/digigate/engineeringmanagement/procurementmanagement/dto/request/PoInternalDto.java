package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.DiscountType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoInternalDto implements IDto {
    private Long id;
    //PROCUREMENT
    private Long requisitionId;
    //LOGISTIC
    private Long pPoId;
    @Size(max = 8000)
    private String tac;
    @Size(max = 8000)
    private String remark;
    private String orderNo;
    private Long csDetailId;
    private List<Long> itemIdList;

    private OrderType orderType = OrderType.PURCHASE;
    private RfqType rfqType = RfqType.PROCUREMENT;
    private InputType inputType = InputType.CS;

    @Size(max = 8000)
    private String shipTo;
    @Size(max = 8000)
    private String invoiceTo;
    @Valid
    private VendorQuotationDto vendorQuotationDto;
    private DiscountType discountType = DiscountType.AMOUNT;
    private Double discount;
    @JsonIgnore
    private VendorQuotation vendorQuotation;
    @JsonIgnore
    private List<VendorQuotationInvoiceDetail> iqItems;
    @Size(max=8000)
    private String companyName;
    @Size(max = 8000)
    private String pickUpAddress;
}
