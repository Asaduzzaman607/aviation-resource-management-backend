package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store_demand_items")
public class StoreDemandItem extends AbstractDomainBasedEntity {

    @Column(name = "quantity_demanded", nullable = false, columnDefinition = "int default 0")
    private Integer quantityDemanded = 0;

    @Column(name = "issued_qty", nullable = false, columnDefinition = "int default 0")
    private Integer issuedQty = 0;

    @Column(name = "priority_type")
    private PriorityType priorityType;

    @Transient
    @Column(length = 8000)
    private String remarks;

    @Column(name = "ipc_cmm", length = 8000)
    private String ipcCmm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;
    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;
    @Column(name = "parent_part_id")
    private Long parentPartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_demand_id")
    private StoreDemand storeDemand;
    @Column(name = "store_demand_id", insertable = false, updatable = false)
    private Long storeDemandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uomId", nullable = false)
    private UnitMeasurement unitMeasurement;
    @Column(name = "uomId", insertable = false, updatable = false)
    private Long uomId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreDemandItem)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreDemandItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
