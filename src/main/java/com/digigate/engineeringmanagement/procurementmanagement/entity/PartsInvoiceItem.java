package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PaymentMode;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "part_invoice_item")
public class PartsInvoiceItem extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_invoice_id", nullable = false)
    private PartsInvoice partsInvoice;
    @Column(name = "part_invoice_id", insertable = false, updatable = false)
    private Long partInvoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id", nullable = false)
    private PartOrderItem partOrderItem;
    @Column(name = "po_item_id", insertable = false, updatable = false)
    private Long poItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_currency_id")
    private Currency paymentCurrency;
    @Column(name = "payment_currency_id", insertable = false, updatable = false)
    private Long paymentCurrencyId;

    @Column(name = "remarks",length = 8000)
    private String remarks;

    @Column(name = "approved_quantity")
    private Integer approvedQuantity = 0;

    @Column(name = "is_partially_approved", columnDefinition = "bit default 0", nullable = false)
    private Boolean isPartiallyApproved = false;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartsInvoiceItem)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((PartsInvoiceItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
