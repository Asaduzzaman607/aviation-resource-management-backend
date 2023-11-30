package com.digigate.engineeringmanagement.configurationmanagement.entity.administration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "approval_settings")
public class ApprovalSetting extends AbstractDomainBasedEntity implements IDto {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id")
    private WorkFlowAction workFlowAction;
    @Column(name = "workflow_action_id", updatable = false, insertable = false)
    private Long workFlowActionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submodule_item_id", nullable = false)
    private ConfigSubmoduleItem configSubmoduleItem;
    @Column(name = "submodule_item_id", updatable = false, insertable = false)
    private Long subModuleItemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApprovalSetting)) return false;
        if (!super.equals(o)) return false;

        ApprovalSetting that = (ApprovalSetting) o;

        if (getWorkFlowAction() != that.getWorkFlowAction()) return false;
        return getSubModuleItemId() != null ? getSubModuleItemId().equals(that.getSubModuleItemId()) : that.getSubModuleItemId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getWorkFlowAction() != null ? getWorkFlowAction().hashCode() : 0);
        result = 31 * result + (getSubModuleItemId() != null ? getSubModuleItemId().hashCode() : 0);
        return result;
    }
}
