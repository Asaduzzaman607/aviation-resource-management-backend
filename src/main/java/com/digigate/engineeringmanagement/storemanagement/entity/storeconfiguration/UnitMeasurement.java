package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "unit_measurements")
public class UnitMeasurement extends AbstractDomainBasedEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnitMeasurement)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        UnitMeasurement unitMeasurement = (UnitMeasurement) o;

        return getId() != null ? getId().equals(unitMeasurement.getId()) : unitMeasurement.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
