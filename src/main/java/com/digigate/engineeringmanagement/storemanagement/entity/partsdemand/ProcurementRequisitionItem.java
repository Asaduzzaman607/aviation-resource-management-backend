package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_requisition_items")
@Entity
public class ProcurementRequisitionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "demand_item_id", nullable = false)
    private StoreDemandItem demandItem;

    @Column(name = "demand_item_id", updatable = false, insertable = false)
    private Long demandItemId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "requisition_id", nullable = false)
    private ProcurementRequisition procurementRequisition;

    @Column(name = "requisition_id", insertable = false, updatable = false)
    private Long requisitionId;

    @Column(name = "quantity")
    private Integer requisitionQuantity;

    @Column(name = "priority")
    private PriorityType priority;

    @Column(name = "remark", columnDefinition = "varchar(2000)")
    private String remark;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", columnDefinition = "bit default 1", nullable = false)
    private Boolean isActive = true;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ProcurementRequisitionItem)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((ProcurementRequisitionItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
