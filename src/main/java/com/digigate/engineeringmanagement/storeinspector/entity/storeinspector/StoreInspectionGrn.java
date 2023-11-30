package com.digigate.engineeringmanagement.storeinspector.entity.storeinspector;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_inspections_grn")
public class StoreInspectionGrn extends AbstractDomainBasedEntity {

    @Column(name = "grn_no", length = 8000, nullable = false, unique = true)
    private String grnNo;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "is_used")
    private Boolean isUsed = Boolean.FALSE;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreInspectionGrn)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreInspectionGrn) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }


}
