package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "procurement_requisitions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcurementRequisition extends AbstractDomainBasedEntity {
    @Column(name = "voucher_no", nullable = false, unique = true)
    private String voucherNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_demand_id", nullable = false)
    private StoreDemand storeDemand;
    @Column(name = "store_demand_id", insertable = false, updatable = false)
    private Long storeDemandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id")
    private User submittedBy;
    @Column(name = "submitted_by_id", insertable = false, updatable = false)
    private Long submittedById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_alive", columnDefinition="bit default 1", nullable = false)
    private Boolean isAlive = Boolean.TRUE;
    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
    @Column(length = 8000)
    private String remarks;
    @Builder.Default
    private OrderType orderType = OrderType.PURCHASE;

    @OneToMany(mappedBy = "procurementRequisition", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<ProcurementRequisitionItem> procurementRequisitionItems = new ArrayList<>();


    @PrePersist
    public void onPrePersist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user= (UserDetailsImpl) auth.getPrincipal();
        this.submittedBy = User.withId(user.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ProcurementRequisition)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((ProcurementRequisition) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
