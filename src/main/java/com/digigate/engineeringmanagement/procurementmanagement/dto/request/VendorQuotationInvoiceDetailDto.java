package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorQuotationInvoiceDetailDto implements IDto {
    private Long id;
    private String condition;
    private String leadTime;
    @NotNull
    private VendorRequestType vendorRequestType;
    private String incoterms;
    private Double unitPrice;
    private Double extendedPrice;
    private Long currencyId;
    private Long partId;
    private Long uomId;
    @Min(value = 0)
    private Integer partQuantity = 0;
    private Long itemId;
    private Long alternatePartId;
    private Boolean isActive;

    private Double moq;
    private Double mlv;
    private Double mov;
    /** GENERIC */
    private ExchangeType exchangeType = ExchangeType.PURCHASE;
    private Double exchangeFee;
    private Double repairCost;
    private Double berLimit;

    private Long partSerialId;

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

    /** DISCOUNT */
    @Builder.Default
    private Double discount = 0.0D;
    private Boolean isDiscount = Boolean.FALSE;
    @JsonIgnore
    private ProcurementRequisitionItem requisitionItem;
    @JsonIgnore
    private PartOrderItem partOrderItem;
    @JsonIgnore
    private InputType inputType;
}
