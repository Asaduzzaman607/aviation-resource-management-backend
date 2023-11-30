package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "vendor_quotation_invoice_fees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VendorQuotationInvoiceFee extends AbstractDomainBasedEntity {
    @Column(name = "vendor_quotation_invoice_fee_id")
    private Long vendorQuotationInvoiceId;
    @Column(name = "vendor_req_type")
    @Enumerated(EnumType.STRING)
    private VendorRequestType vendorRequestType;
    @Column(nullable = false)
    private String feeName;
    @Column(nullable = false)
    private Double feeCost;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "currency_id", updatable = false, insertable = false)
    private Long currencyId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VendorQuotationInvoiceFee)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((VendorQuotationInvoiceFee) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
