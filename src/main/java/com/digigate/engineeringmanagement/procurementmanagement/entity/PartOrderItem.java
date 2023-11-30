package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "part_order_items")
public class PartOrderItem extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iq_item_id", nullable = false)
    private VendorQuotationInvoiceDetail iqItem;
    @Column(name = "iq_item_id", insertable = false, updatable = false)
    private Long iqItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_order_id", nullable = false)
    private PartOrder partOrder;
    @Column(name = "part_order_id", insertable = false, updatable = false)
    private Long partOrderId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartOrderItem)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((PartOrderItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
