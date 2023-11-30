package com.digigate.engineeringmanagement.storeinspector.entity.storeinspector;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionCriterionStatus;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inspections_criterions")
public class InspectionCriterion extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private StoreInspection storeInspection;
    @Column(name = "inspection_id", insertable = false, updatable = false)
    private Long inspectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_id", nullable = false)
    private InspectionChecklist inspectionChecklist;
    @Column(name = "description_id", insertable = false, updatable = false)
    private Long descriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_status", nullable = false)
    private InspectionCriterionStatus inspectionStatus;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof InspectionCriterion)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((InspectionCriterion) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
