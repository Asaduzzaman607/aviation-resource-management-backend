package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "part_wise_uom")
public class PartWiseUom extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partId", nullable = false)
    private Part part;
    @Column(name = "partId", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uomId", nullable = false)
    private UnitMeasurement unitMeasurement;
    @Column(name = "uomId", insertable = false, updatable = false)
    private Long uomId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartWiseUom)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((PartWiseUom) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
