package com.digigate.engineeringmanagement.configurationmanagement.entity.administration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_settings")
public class NotificationSetting extends AbstractDomainBasedEntity {

    @Column(name = "submodule_item_id", nullable = false)
    private Long submoduleItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id")
    private WorkFlowAction workFlowAction;

    @Column(name = "workflow_action_id", updatable = false, insertable = false)
    private Long workFlowActionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationSetting)) return false;
        if (!super.equals(o)) return false;

        NotificationSetting that = (NotificationSetting) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}