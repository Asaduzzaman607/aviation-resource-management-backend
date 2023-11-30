package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.procurementmanagement.constant.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.DEFAULT_PRICE;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorQuotationInvoiceDetailViewModel {
    private Long id;
    private Long itemId;
    private String condition;
    private String leadTime;
    private String incoterms;
    private Double unitPrice = DEFAULT_PRICE;
    private Double extendedPrice;
    private VendorRequestType vendorRequestType;
    private Long currencyId; // TODO: value 1 only for tweaking, remove it
    private String currencyCode;
    private Boolean isActive;
    private Long partId;
    private String partNo;
    private String partDescription;
    private Long unitMeasurementId;
    private String unitMeasurementCode;
    private Long alternatePartId;
    private String alternatePartNo;
    private String alternatePartDescription;
    private Long alternateUnitMeasurementId;
    private String alternateUnitMeasurementCode;
    private Integer partQuantity = 0;

    private Double moq;
    private Double mlv;
    private Double mov;

    /** GENERIC */
    private ExchangeType exchangeType;
    private Double exchangeFee;
    private Double repairCost;
    private Double berLimit;

    private Long partSerialId;
    private Long serialId;
    private String serialNo;

    /** LOAN */
    private LocalDate loanStartDate;
    private LocalDate loanEndDate;
    private LoanStatus loanStatus;

    /** REPAIR */
    private RepairType repairType;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private Integer evaluationFee;
    private AdditionalFeeType additionalFeeType;
    private Double raiScrapFee;
    private String reasonRemoved;

    /** DISCOUNT */
    private Double discount = 0.0D;
    private Boolean isDiscount = Boolean.FALSE;

    /** VENDOR SERIALS */
    private String vendorSerials;
}
