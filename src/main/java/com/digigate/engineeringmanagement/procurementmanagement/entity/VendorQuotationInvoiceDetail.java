package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.procurementmanagement.constant.*;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "vendor_quotation_invoice_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorQuotationInvoiceDetail extends AbstractDomainBasedEntity {
    @Column(name = "vendor_quotation_invoice_detail_id")
    private Long vendorQuotationInvoiceId;

    @Column(name = "vendor_req_type")
    @Enumerated(EnumType.STRING)
    private VendorRequestType vendorRequestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ProcurementRequisitionItem requisitionItem;
    @Column(name = "item_id", insertable = false, updatable = false)
    private Long requisitionItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternate_part_id")
    private Part alternatePart;
    @Column(name = "alternate_part_id", insertable = false, updatable = false)
    private Long alternatePartId;

    /** FOR LOGISTIC */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id")
    private PartOrderItem poItem;
    @Column(name = "po_item_id", insertable = false, updatable = false)
    private Long poItemId;

    private String condition;
    private String leadTime;
    private String incoterms;
    private Double unitPrice;
    private Double extendedPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "currency_id", updatable = false, insertable = false)
    private Long currencyId;
    private Double moq;
    private Double mlv;
    private Double mov;
    @Builder.Default
    private Boolean isDiscount = Boolean.FALSE;
    /** MANUAL PO */
    @Builder.Default
    private Integer partQuantity = 0;

    /** GENERIC FIELD */
    @Builder.Default
    private ExchangeType exchangeType = ExchangeType.PURCHASE;
    private Double exchangeFee;
    private Double repairCost;
    private Double berLimit;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_serial_id")
    private StorePartSerial partSerial;
    @Column(name = "part_serial_id", insertable = false, updatable = false)
    private Long partSerialId;

    /** LOAN */
    private LocalDate loanStartDate;
    private LocalDate loanEndDate;
    private LoanStatus loanStatus;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uom_id")
    private UnitMeasurement unitMeasurement;
    @Column(name = "uom_id", insertable = false, updatable = false)
    private Long uomId;

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

    /** VENDOR SERIALS */
    private String vendorSerials;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VendorQuotationInvoiceDetail)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((VendorQuotationInvoiceDetail) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}