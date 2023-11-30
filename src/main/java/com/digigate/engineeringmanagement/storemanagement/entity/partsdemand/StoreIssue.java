package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.StoreStockRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "store_issues")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreIssue extends AbstractDomainBasedEntity {

    @Column(name = "voucher_no", unique = true, nullable = false)
    private String voucherNo;

    @Column(name = "stock_room_type")
    @Enumerated(EnumType.STRING)
    private StockRoomType stockRoomType;

    @Column(name = "registration", length = 8000)
    private String registration;

    private PartClassification partClassification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    @Column(name = "submitted_by", insertable = false, updatable = false)
    private Long submittedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_room_id")
    private StoreStockRoom storeStockRoom;
    @Column(name = "stock_room_id", insertable = false, updatable = false)
    private Long storeStockRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_demand_id")
    private StoreDemand storeDemand;
    @Column(name = "store_demand_id", insertable = false, updatable = false)
    private Long storeDemandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date")
    private LocalDate updateDate;
    @Column(length = 8000)
    private String remarks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storeIssue")
    private Set<StoreIssueItem> storeIssueItemSet = new HashSet<>();

    private Boolean isReturnApproved = false;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreIssue)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((StoreIssue) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
