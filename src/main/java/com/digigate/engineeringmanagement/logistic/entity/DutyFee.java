package com.digigate.engineeringmanagement.logistic.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoiceItem;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "duty_fees")
public class DutyFee extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_invoice_item_id", nullable = false)
    private PartsInvoiceItem partsInvoiceItem;

    @Column(name = "part_invoice_item_id", insertable = false, updatable = false)
    private Long partsInvoiceItemId;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DutyFee)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((DutyFee) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}

