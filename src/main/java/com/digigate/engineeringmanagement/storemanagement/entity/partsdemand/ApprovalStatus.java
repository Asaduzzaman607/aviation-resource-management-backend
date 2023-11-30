package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "approval_status")
public class ApprovalStatus extends AbstractDomainBasedEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_type")
    private ApprovalStatusType approvalStatusType;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;

    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ApprovalStatus)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((ApprovalStatus) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
