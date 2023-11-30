package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
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
@Table(name = "store_issues_items")
public class StoreIssueItem extends AbstractDomainBasedEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "demand_item_id", nullable = false)
    private StoreDemandItem storeDemandItem;

    @Column(name = "demand_item_id", insertable = false, updatable = false)
    private Long storeDemandItemId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "issue_id", nullable = false)
    private StoreIssue storeIssue;

    @Column(name = "issue_id", insertable = false, updatable = false)
    private Long storeIssueId;

    @Column(name = "quantity_issued", nullable = false)
    private Integer issuedQuantity;

    @Column(name = "card_line_no", length = 100)
    private String cardLineNo;

    @Column(name = "remark", columnDefinition = "varchar(2000)")
    private String remark;

    @Column(name = "priority")
    private PriorityType priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uomId", nullable = false)
    private UnitMeasurement unitMeasurement;
    @Column(name = "uomId", insertable = false, updatable = false)
    private Long uomId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreIssueItem)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreIssueItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
