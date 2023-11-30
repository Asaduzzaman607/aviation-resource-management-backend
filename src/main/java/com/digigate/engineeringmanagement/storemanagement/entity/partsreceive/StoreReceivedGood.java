package com.digigate.engineeringmanagement.storemanagement.entity.partsreceive;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "store_received_goods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreReceivedGood extends AbstractDomainBasedEntity {
    /**
     * Order id will be added!
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_stock_inward_id", nullable = false)
    private StoreStockInward storeStockInward;
    @Column(name = "store_stock_inward_id", insertable = false, updatable = false)
    private Long storeStockInwardId;
    @Column(name = "gr_date")
    private LocalDate grDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private ProcurementRequisition procurementRequisition;
    @Column(name = "requisition_id", updatable = false, insertable = false)
    private Long requisitionId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreReceivedGood)) return false;
        return this.getId() != 0 && this.getId().equals(((StoreReceivedGood) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
