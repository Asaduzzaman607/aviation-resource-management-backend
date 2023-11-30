package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.procurementmanagement.constant.ExchangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CsqDetailResponseDto {
    private Long detailId;
    private Long partId;
    private Double unitPrice;
    private Double moq;
    private Double mlv;
    private Double mov;
    private String leadTime;
    private String condition;
    private String currencyCode;
    private String incoterms;
    private ExchangeType exchangeType;
    private Double exchangeFee;
    private Double repairCost;
    private Double berLimit;
    private Double discount;
    private String vendorUomCode;
    private Integer vendorPartQuantity;
}
