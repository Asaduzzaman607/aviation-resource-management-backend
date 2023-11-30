package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "quote_requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuoteRequest extends AbstractDomainBasedEntity {
    @Column(name = "rfq_no", nullable = false, unique = true)
    private String rfqNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id")
    private ProcurementRequisition procurementRequisition;
    @Column(name = "requisition_id", updatable = false, insertable = false)
    private Long requisitionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_order_id")
    private PartOrder partOrder;
    @Column(name = "part_order_id", updatable = false, insertable = false)
    private Long partOrderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;
    @Column(name = "is_rejected", columnDefinition = "bit default 0", nullable = false)
    private Boolean isRejected = Boolean.FALSE;
    @Column(name = "rejected_desc")
    private String rejectedDesc;
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "rfq_type", nullable = false)
    private RfqType rfqType = RfqType.PROCUREMENT;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;
    @Builder.Default
    private InputType inputType = InputType.CS;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof QuoteRequest)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((QuoteRequest) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
