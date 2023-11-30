package com.digigate.engineeringmanagement.storeinspector.entity.storeinspector;

import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inspection_checklists")
public class InspectionChecklist extends AbstractDomainBasedEntity {

    @Column(name = "description", nullable = false, length = 8000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_action_id", nullable = false)
    private WorkFlowAction workFlowAction;

    @Column(name = "workflow_action_id", insertable = false, updatable = false)
    private Long workFlowActionId;

    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = Boolean.FALSE;

    @Column(name = "rejected_desc")
    private String rejectedDesc;

    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;

    @Enumerated(EnumType.STRING)
    private VendorWorkFlowType workflowType;
    @Column(name = "submodule_item_id")
    private Long submoduleItemId;
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof InspectionChecklist)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((InspectionChecklist) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
