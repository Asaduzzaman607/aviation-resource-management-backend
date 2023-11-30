package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PaymentMode;
import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartsInvoiceItemReqDto implements IDto {
    Long id;
    @NotNull
    private Long paymentCurrencyId;
    @NotNull
    private PaymentMode paymentMode;
    private String remarks;
    private Integer approvedQuantity;
    private Integer quantity;
}
